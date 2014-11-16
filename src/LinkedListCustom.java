import java.util.Iterator;

public class LinkedListCustom<T extends Comparable<T>> implements Iterable<T> {
    private Node root;

    LinkedListCustom() {
        root = null;
    }

    public static void main(String[] args) {

    }

    public void add(T elem) {
        root = new Node(elem, root);
    }

    public Iterator<T> iterator() {
        return new LinkedListIterator();
    }

    public void mergeSort() {
        root = mergeSort(root);
    }

    public Node getMid(Node head) {
        if (head == null) {
            return null;
        }

        Node a, b;
        a = b = head;
        while (b.next != null && b.next.next != null) {
            a = a.next;
            b = b.next.next;
        }

        return a;
    }

    private Node mergeSort(Node head) {
        if (head == null || head.next == null) {
            return head;
        }

        Node middle = getMid(head);
        Node sHalf = middle.next;
        middle.next = null;

        return merge(mergeSort(head), mergeSort(sHalf));
    }

    public Node merge(Node a, Node b) {
        Node head = new Node();
        Node currentNode = head;

        while (a != null && b != null) {
            if (a.item.compareTo(b.item) > 0) {
                currentNode.next = a;
                a = a.next;
            } else {
                currentNode.next = b;
                b = b.next;
            }

            currentNode = currentNode.next;
        }

        currentNode.next = (a == null) ? b : a;
        return head.next;
    }

    private class Node {
        T item;
        Node next;

        Node(T i, Node n) {
            item = i;
            next = n;
        }

        public Node() {
        }
    }

    private class LinkedListIterator implements Iterator<T> {

        Node current;

        LinkedListIterator() {
            current = root;
        }

        public boolean hasNext() {
            return current != null;
        }

        public T next() {
            T output = current.item;
            current = current.next;
            return output;
        }

        public void remove() {
            throw new UnsupportedOperationException("remove");
        }
    }
}
