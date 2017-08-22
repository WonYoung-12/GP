package com.example.kwy2868.practice.util;

import android.os.Handler;
import android.util.Log;

import com.example.kwy2868.practice.model.AnalyzedWave;
import com.example.kwy2868.practice.model.Error;

import java.util.Observable;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;

/**
 * sound 입력 및 처리가 이뤄지는 클래스
 * 이 어플의 핵심
 */
public class SoundAnalyzer extends Observable {
    public static final int AUDIO_SAMPLING_RATE    = 512;
	public static final int AUDIO_DATA_SIZE        = 600;

    private static final double MPM = 0.7;
    private static final double MAX_STDEV_OF_MEAN_FREQUENCY         = 2.0;      // 계산된 표준편차 값이 이보다 크면 쓰레기값
    private static final double MAX_POSSIBLE_FREQUENCY              = 1024.0;   // 가능한 최대 주파수
    private static final double LOUDNESS_THRESHOLD                  = 0.0;     // 이 주파수 이하는 그냥 버림
    private static final double IGNORE_WAVELENGTH_SAMPLES_PERCENT   = 0.2;      // 입력된 주파수중 이만큼 버리고 시작

    private static double notifyRateinS = 0.15; // 얼마나 자주 주파수를 가져와서 처리할것인가
    private static double minNotifyRate = 0.4; // 최소값
    private static double maxNotifyRate
            = (double) AUDIO_DATA_SIZE / (double) AUDIO_SAMPLING_RATE; // 최대값

	private final CircularBuffer audioData;

	private Lock analyzingData;
	private double[] audioDataAnalytics;
	private DoubleFFT_1D fft_method;    // 고속 푸리에 변환을 위해 사용
	private int wavelengths;
	private double[] wavelength;
	private int elementsRead = 0;

	private boolean shouldAudioReaderThreadDie;
	private Thread audioReaderThread;
	private Handler handler;

	public SoundAnalyzer() throws Exception {
        // Setting up AudioRecord class.
        audioDataAnalytics = new double[4 * AUDIO_DATA_SIZE + 100];
		wavelength = new double[AUDIO_DATA_SIZE];
		audioData = new CircularBuffer(AUDIO_DATA_SIZE);
		analyzingData = new ReentrantLock();
		fft_method = new DoubleFFT_1D(AUDIO_DATA_SIZE);
        handler = new Handler();
        //start();
	}
	
	public void start() {
        handler.post(periodicTask);
	}

	public synchronized void startAudioReaderThread(final int size, final short[] data) {
		shouldAudioReaderThreadDie = false;

		audioReaderThread = new Thread(new Runnable() {
			@Override
			public void run() {
                soundReadTask(size, data);
			}
		});
		audioReaderThread.setDaemon(false);
		audioReaderThread.start();
	}

    private volatile boolean active = false;
    private void soundReadTask(int size, short[] data) {          // 사용자의 소리를 입력받는 Thread 데몬쓰레드로 Background에서 별다른 입력이 없으면 계속 사용자 입력을 받고있는다.
        while(!shouldAudioReaderThreadDie) {
            while(active) {
                // Thread Lock 없이 wait.
            }
            active = false;
            int shortsRead = size;
            if (shortsRead < 0) {
                Log.e("SoundAnalyzer", "오디오 데이터를 읽을 수 없습니다.");
            } else {
                for (int i = 0; i < shortsRead; i++) {
                    audioData.push(data[i]);
                }
            }
        }
    }

	private void stopAudioReaderThread() {
		shouldAudioReaderThreadDie = true;
		try {
			audioReaderThread.join();
		} catch(Exception e) {
			Log.e("SoundAnalyzer", "AudioRenderThread 를 Join 할 수 없습니다.", e);
		}
	}
	
	public void stop() {
		stopAudioReaderThread();
	}

	
	private AnalyzedWave analyzedResult;

	private Runnable periodicTask = new TimerTask() {
        @Override
        public void run() {
            notifyObservers(analyzedResult);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    if(!analyzingData.tryLock()) {      // 분석하려는 데이터가 Lock이 걸려 있지 않다면 사용한다.
                        notifyRateinS = Math.min(notifyRateinS + 0.01, minNotifyRate);  // 분석할만한 데이터가 들어왔다면 다음 호출까지 시간을 증가시킨다.
                        return;
                    } else {
                        notifyRateinS = Math.max(notifyRateinS - 0.001, maxNotifyRate); // 분석할만한 데이터가 없다면 호출시기를 앞당긴다.
                    }

                    analyzedResult = getFrequency();    // 주파수 처리부분
                    Log.d("SoundAnalyzer", "freq : " + analyzedResult.frequency);
                    analyzingData.unlock(); // 분석 끝났으므로 unLock
                    setChanged();   //이 클래스자체에 implement한 옵저버에 변경사항을 알려줌
                }
            }).start();
        }
    };

	private int currentFftMethodSize = -1;
	
    private double hanning(int n, int N) { // 등간격 자료를 0.25, 0.5, 0.25의 가중치를 이용하여 평활화시키는 기법.
        return 0.5 * (1.0 - Math.cos(2 * Math.PI * (double) n / (double) (N-1)));
	}
	
	private void computeAutocorrelation() {
        /**
         *  자세한 사항은 고속 푸리에 변환에 대한 지식이 필요합니다.
         *  고속 푸리에변환은 푸리에변환을 더 빨리 할 수 있는 기술입니다.
         *  푸리에 급수는 모든 주기적인 함수 (주기적인 소리) 는 여러 항의 삼각함수로 표현될수 있고
         *  이렇게 만드는것을 푸리에 변환이라고 함
         *  소리는 서로 다른 주파수와 크기를 갖는 여러 다른 소리들의 합성으로 이루어져 있음
         *  따라서 분석하려는 소리에서 그것을 구성하는 각 소리들의 주파수와 진폭을 구하면
         *  이 소리에 대한 정보를 알 수 있음
         */

		if (elementsRead * 2 != currentFftMethodSize) {
			fft_method = new DoubleFFT_1D(elementsRead * 2);
			currentFftMethodSize = elementsRead * 2;
		} // 주파수 처리하기 위한 메모리공간 할당
		
		// 읽어들인 소리를 hanning 윈도우를 사용 필터링 .
		for (int i = elementsRead - 1; i >= 0; i--) {
			audioDataAnalytics[i * 2]= audioDataAnalytics[i] * hanning(i, elementsRead);
			audioDataAnalytics[i * 2 + 1] = 0;
		}
		for (int i = elementsRead * 2; i < audioDataAnalytics.length; i++) {
            audioDataAnalytics[i] = 0;
        }
		
		// 푸리에 변환 시행 .
		fft_method.complexInverse(audioDataAnalytics, false);

		// 모든 주파수를 거리로 변환
		for (int i = 0; i < elementsRead; i++) {
			audioDataAnalytics[i * 2] = Math.pow(audioDataAnalytics[i * 2], 2) + Math.pow(audioDataAnalytics[i * 2 + 1], 2);
			audioDataAnalytics[i * 2 + 1] = 0;
		}
		for (int i = elementsRead * 2; i < audioDataAnalytics.length; i++) {
            audioDataAnalytics[i] = 0;
        }

        // 가장 첫 index는 0.
		audioDataAnalytics[0] = 0;
		
		// 푸리에 역변환 시행
		fft_method.complexForward(audioDataAnalytics);
		
		// 결과중 사용할 부분 가져와서 저장
		for (int i = 0; i < elementsRead; i++) {
            audioDataAnalytics[i] = audioDataAnalytics[i * 2];
        }
		for (int i = elementsRead; i < audioDataAnalytics.length; i++) {
            audioDataAnalytics[i] = 0;
        }
	}

	private double getMeanWavelength() {
		double mean = 0;
		for (int i = 0; i < wavelengths; i++) {
            mean += wavelength[i];
        }
		return mean / (double) wavelengths;
	}
	
	private double getStDevOnWavelength() {
		double variance = 0;
        double mean = getMeanWavelength();

		for (int i = 1; i < wavelengths; i++) {
            variance += Math.pow(wavelength[i] - mean, 2);
        }
		variance /= (double) (wavelengths - 1);
		return Math.sqrt(variance);
	}
	
	private void removeFalseSamples() {
        if (wavelengths <= 2) {
            return;
        }

		int samplesToBeIgnored = (int) (IGNORE_WAVELENGTH_SAMPLES_PERCENT * wavelengths);
		do {
			double mean = getMeanWavelength();

			int best = -1;
			for (int i = 0; i < wavelengths; i++) {
                if (best == -1 || Math.abs(wavelength[i] - mean) > Math.abs(wavelength[best] - mean)) {
                    best = i;
                }
            }

			wavelength[best] = wavelength[wavelengths - 1];
			wavelengths--;
		} while(getStDevOnWavelength() > MAX_STDEV_OF_MEAN_FREQUENCY
                && samplesToBeIgnored-- > 0 && wavelengths > 2);
	}

	private AnalyzedWave getFrequency() {
        elementsRead = audioData.getElements(audioDataAnalytics, 0, AUDIO_DATA_SIZE);   // 지금까지 읽은 audio data 가져옴
        double loudness = 0.0;

        for (int i = 0; i < elementsRead; i++) {
            loudness += Math.abs(audioDataAnalytics[i]);    // 가져온데이터를 다더함
        }
        loudness /= elementsRead;   // 나눔 (평균구함)

        if (loudness < LOUDNESS_THRESHOLD) {
            Log.d("SoundAnalyzer", "too-quite");
            return new AnalyzedWave(Error.TOO_QUIET);    // 가져온 소리의 평균이 Threshold 값보다 작으면 버림
        }

        computeAutocorrelation();   // 주파수 분할

        double maximum = 0;
        for (int i = 1; i < elementsRead; i++) {
            maximum = Math.max(audioDataAnalytics[i], maximum);
        }

        int lastStart = -1;
        wavelengths = 0;
        boolean passedZero = true;
        for (int i = 0; i < elementsRead; i++) {
            if (audioDataAnalytics[i] * audioDataAnalytics[i + 1] <= 0) {
                passedZero = true;
            }   // 영점교차 판단

            if (passedZero &&   // 영점교차가 일어났고
                    audioDataAnalytics[i] > MPM * maximum &&    // 최대값 * 보정값 보다 진폭이 크고
                    audioDataAnalytics[i] > audioDataAnalytics[i + 1]) {    // 크기가 감소하면
                if (lastStart != -1) {  // 처음이아님
                    wavelength[wavelengths++] = i - lastStart;  // 주기의 길이 체크
                }
                lastStart = i;
                passedZero = false;
                maximum = audioDataAnalytics[i];
            }
        }

        /*if (wavelengths < 2) { // 측정된 주기가 2이하면 입력이 Zero로 판단
            Log.d("SoundAnalyzer", "zero-samples");
            return new AnalyzedSound(Error.ZERO_SAMPLES);
        }*/
        removeFalseSamples();

        double mean = getMeanWavelength();
        double stdev = getStDevOnWavelength();
        double calculatedFrequency = (double) AUDIO_SAMPLING_RATE / mean;

        if (stdev >= MAX_STDEV_OF_MEAN_FREQUENCY) {
            return new AnalyzedWave(Error.BIG_VARIANCE);
        } else if (calculatedFrequency > MAX_POSSIBLE_FREQUENCY) {
            return new AnalyzedWave(Error.BIG_FREQUENCY);
        } else {
            return new AnalyzedWave(calculatedFrequency);
        }
    }
}
