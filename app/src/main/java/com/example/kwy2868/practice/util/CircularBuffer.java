package com.example.kwy2868.practice.util;

/**
 * 배열의 앞 쪽 index의 값을 제거할 경우 index를 재배열해야하는데,
 * 이 때 발생하는 비용을 줄이기 위하여 환형버퍼를 사용한다.
 */
public class CircularBuffer {
	private final short[] array;
	private int head;
	private final int size;
	private int availableElements;
	
	public CircularBuffer(int size) {
		this.size = size;
		array = new short[size];
		head = 0;
		availableElements = 0;
	}
	
	public synchronized void push(short x) {
		array[head++] = x;
		if (head >= size) {
			head-=size;
		}
		availableElements = Math.min(availableElements + 1, size);
	}
	
	public synchronized int getElements(double[] result, int offset, int maxElements) {
		int toRead = Math.min(maxElements, availableElements);
		int current = head - 1;
		for (int i = offset + toRead - 1; i >= offset; --i) {
			if (current < 0) {
                current+=size;
            }
			result[i] = array[current--];
		}
		return toRead;
	}
}
