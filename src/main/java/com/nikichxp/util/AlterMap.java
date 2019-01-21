package com.nikichxp.util;

public class AlterMap {

	private Node first = null;
	private Node last = null;
	private int size = 0;
	private long mean = 0;

	public Long get(int key) {

		Node ptr;

		// forward search
		if (key < mean / size) {

			ptr = first;

			for (int i = 0; i <= size / 2; i++) {
				if (ptr.key == key) {
					return ptr.value;
				}
				ptr = ptr.next;
			}

		} else { //backward search
			ptr = last;

			for (int i = 0; i <= size / 2; i++) {
				if (ptr.key == key) {
					return ptr.value;
				}
				ptr = ptr.prev;
			}
		}

		return (ptr.key == key) ? ptr.value : null;
	}

	public void put(int key, long value) {

		Node ptr = first;

		while (ptr != null) {
			if (ptr.key == key) {
				// search for next free space
				// skip nearby
				while (ptr.key == ptr.next.key - 1) {
					ptr = ptr.next;
				}
				addNode(ptr, ptr.key + 1, value);
				return;
			} else if (ptr.key < key && ptr.next.key > key) {
				addNode(ptr, key, value);
				return;
			}
			ptr = ptr.next;
		}

		if (size == 0) {
			first = new Node(key, value);
			last = first;
			size++;
			return;
		}

		// target element is last element & last key is not equal our key

		addNode(last, (key != last.key) ? key : key + 1, value);
	}

	private void addNode(Node ptr, int key, long value) {
		Node node = new Node(key, value);
		node.next = ptr.next;
		ptr.next = node;
		size++;
		mean += key;

		if (node.next == null) {
			last = node;
		} else {
			node.prev = ptr;
			node.next.prev = node;
		}
	}

	/**
	 * Node, according to task, is always a int-long pair
	 */
	static class Node {

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
