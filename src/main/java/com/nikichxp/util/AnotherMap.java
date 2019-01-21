package com.nikichxp.util;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class AnotherMap {

	public int size = 0;

	public double splitRatio = 2.0;

	private Segment first = new Segment();

	public void put(int key, long value) {

		Segment segment = first;

		while (key > segment.endKey) {
			segment = segment.next;
		}

		boolean put = false;
		int probe = 0;

		do {
			try {
				segment.put(key, value, probe);
				put = true;
			} catch (HashCollisionException ignored) {
				key = Integer.toString(key).hashCode();
				probe++;
			}
		} while (!put);

		size++;

		if (segment.size > Math.sqrt(size) * splitRatio) {
			segment.split();
		}
	}

	/**
	 * In fact, we do have multiple possible variants in a single possible key
	 *
	 * @param key key for this map
	 * @return list of possible values been put by this key
	 */
	public List<Long> get(int key) {

		List<Node> nodes = new LinkedList<>();

		Node node = null;
		int probe = 0;
		do {
			Segment ptr = first;

			System.out.println(ptr);
			while (ptr.startKey < key) {
				ptr = ptr.next;
			}

			if (ptr.endKey < key) {
				return null;
			}

			node = ptr.getByKey(key);

			if (node != null && node.probe == probe) {
				nodes.add(node);
			}

			key = Integer.toString(key).hashCode();
			probe++;

		} while (node != null);

		return nodes.stream().map(n -> n.value).collect(Collectors.toList());
	}

	private class Segment {
		int startKey = Integer.MIN_VALUE;
		int endKey = Integer.MAX_VALUE;

		Segment next;
		int size = 0;

		Node first;

		private Node getByKey(int key) {
			Node ptr = first;
			if (ptr == null || ptr.key > key) {
				return null;
			}
			while (ptr.key < key) {
				ptr = ptr.next;
			}
			return (ptr.key == key) ? ptr : null;
		}

		private void put(int key, long value, int probe) throws HashCollisionException {
			if (startKey > key || key > endKey) {
				throw new IllegalArgumentException("This value cannot be here");
			}

			Node ptr = first;

			if (ptr == null) {
				first = new Node(key, value, probe);
				size++;
				return;
			}

			if (key < first.key) {
				Node node = new Node(key, value, probe);
				node.next = first;
				node.prev = first.prev;
				first = node;
				node.next.prev = node;
				node.prev.next = node;
				size++;
				return;
			}

			do {

				if (ptr.key == key) {
					throw new HashCollisionException();
				}

				if (ptr.next == null || ptr.next.key > key) {
					addNodeAfter(ptr, key, value, probe);
					return;
				}

				ptr = ptr.next;

			} while (key < ptr.value);
		}

		private Node addNodeAfter(Node ptr, int key, long value, int probe) {
			Node node = new Node(key, value, probe);
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

		int probe = 0;

		Node(int key, long value, int probe) {
			this.key = key;
			this.value = value;
			this.probe = probe;
		}

	}

	private class HashCollisionException extends Exception {
	}

}
