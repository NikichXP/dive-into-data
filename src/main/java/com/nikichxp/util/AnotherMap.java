package com.nikichxp.util;

public class AnotherMap {

	public int size = 0;

	public int minSegmentSize = 10;
	public double splitRatio = 2.0;

	private Segment first = new Segment();

	public void put(int key, long value) {

		Segment segment = first;

		while (key > segment.endKey) {
			segment = segment.next;
		}

		segment.put(key, value);
		size++;

		if (segment.size > Math.sqrt(size) * splitRatio) {
			segment.split();
		}
	}

	public Long get(int key) {
		Segment ptr = first;

		while (ptr.startKey < key) {
			ptr = ptr.next;
		}

		if (ptr.endKey < key) {
			return null;
		}

		return ptr.getByKey(key);
	}

	private class Segment {
		int startKey = Integer.MIN_VALUE;
		int endKey = Integer.MAX_VALUE;

		Segment next;
		int size = 0;

		Node first;

		private Long getByKey(int key) {
			Node ptr = first;
			if (ptr == null || ptr.key > key) {
				return null;
			}
			while (ptr.key < key) {
				ptr = ptr.next;
			}
			return (ptr.key == key) ? ptr.value : null;
		}

		private void put(int key, long value) {
			if (startKey > key || key > endKey) {
				throw new IllegalArgumentException("This value cannot be here");
			}

			Node ptr = first;

			if (ptr == null) {
				first = new Node(key, value);
				size++;
				return;
			}

			if (key < first.key) {
				Node node = new Node(key, value);
				node.next = first;
				node.prev = first.prev;
				first = node;
				node.next.prev = node;
				node.prev.next = node;
				size++;
				return;
			}

			do {

				if (ptr.next == null || ptr.next.key > key) {
					addNodeAfter(ptr, key, value);
					return;
				}

				ptr = ptr.next;

			} while (key < ptr.value);

			if (ptr.key == key) {
				ptr = first;

				while (ptr.key == ptr.key + 1) {
					ptr = ptr.next;
				}

				Node node = addNodeAfter(ptr, ptr.key + 1, value);

				if (node.key <= this.endKey) {
					return; // this is okay
				}

				this.size--;

				// this means we have a serious trouble. value is in another segment. let's find the segment which have
				//  that node!

				Segment segment = this;

				while (node.key < segment.startKey) {
					segment = segment.next;
				}

				segment.size++;
			}
		}

		private Node addNodeAfter(Node ptr, int key, long value) {
			Node node = new Node(key, value);
			node.next = ptr.next;
			ptr.next = node;
			node.prev = ptr;

			if (node.next != null) {
				node.next.prev = node;
			}

			this.size++;

			return node;
		}

		void split() {
			Segment another = new Segment();

			Node ptr = first;

			int splitSize = size / 2;

			for (int i = 0; i < splitSize; i++) {
				ptr = ptr.next;
			}

			another.first = ptr;
			another.startKey = ptr.key;
			another.endKey = this.endKey;
			this.endKey = ptr.key - 1;
			another.next = this.next;
			this.next = another;

			another.size = this.size - splitSize;
			this.size -= splitSize;
		}


	}

	/**
	 * Node, according to task, is always a int-long pair
	 */
	private class Node {

		int key;
		long value;
		Node next = null;
		Node prev = null;

		Node(int key, long value) {
			this.key = key;
			this.value = value;
		}

	}

}
