import java.util.Iterator;

public class FourthAssignment {
    public class LinkedList<T extends Comparable<T>> implements Iterable<T> {
        private Node root;
        private int size;

        public LinkedList() {
            root = null;
            size = 0;
        }

        public void add(T elem) {
            size++;
            root = new Node(elem, root);
        }

        public Iterator<T> iterator() {
            return new LinkedListIterator();
        }


        private boolean greater(Node a, Node b) {
            return a.item.compareTo(b.item) > 0;
        }


        public void mergeSort() {
            root = mergeSort(root);
        }

        private Node mergeSort(Node head) {
            // TODO FIX STATIC-NESS
            if (head == null || head.next == null) {
                return head;
            }

            Node findSize = head;
            while (findSize != null) {
                findSize = findSize.next;
            }

            int middle = (size / 2);

            Node leftRoot = root;
            Node rightRoot = null;
            Node findMiddle = root;
            int middleCounter = 0;

            // Finds middle of the list and breaks it into two sub lists
            while (findMiddle != null) {
                middleCounter++;
                Node nextNode = findMiddle.next;

                if (middleCounter == middle) {
                    findMiddle.next = null;
                    rightRoot = nextNode;
                }
                findMiddle = nextNode;
            }

            // Recursively mergeSorts the two sub lists
            System.out.println(1);
            Node leftHalf = mergeSort(leftRoot);
            Node rightHalf = mergeSort(rightRoot);
            return merge(leftHalf, rightHalf);
        }

        private Node merge(Node leftRoot, Node rightRoot) {
            Node leftListNode = leftRoot;
            Node rightListNode = rightRoot;

            // Serves as the root for the merged List
            Node newMergedHead = null;
            Node tempNode;
            Node currentRoot = null;

            while (leftListNode != null || rightListNode != null) {

                if (leftListNode == null) {

                    //TODO Sort stuff from rightRoot until we're done

                    if (newMergedHead == null) {
                        newMergedHead = rightListNode;
                        currentRoot = newMergedHead;
                    } else {
                        tempNode = rightListNode;
                        rightListNode.next = currentRoot;
                        currentRoot = rightListNode;
                        rightListNode = tempNode.next;
                    }
                } else if (rightListNode == null) {
                    //TODO Sort stuff from leftRoot until we're done
                    if (newMergedHead == null) {
                        newMergedHead = leftListNode;
                        currentRoot = newMergedHead;
                    } else {
                        tempNode = leftListNode;
                        leftListNode.next = currentRoot;
                        currentRoot = leftListNode;
                        leftListNode = tempNode.next;
                    }
                } else {
                    if (greater(rightListNode, leftListNode)) {
                        //TODO Sort stuff from leftRoot once
                        if (newMergedHead == null) {
                            newMergedHead = leftListNode;
                            currentRoot = newMergedHead;
                            leftListNode = leftListNode.next;
                        } else {
                            tempNode = leftListNode;
                            leftListNode.next = currentRoot;
                            currentRoot = leftListNode;
                            leftListNode = tempNode.next;
                        }
                    } else if (greater(leftListNode, rightListNode)) {
                        //TODO Sort stuff from rightRoot once
                        if (newMergedHead == null) {
                            newMergedHead = rightListNode;
                            currentRoot = newMergedHead;
                            rightListNode = rightListNode.next;
                        } else {
                            tempNode = rightListNode;
                            rightListNode.next = currentRoot;
                            currentRoot = rightListNode;
                            rightListNode = tempNode.next;
                        }
                    } else {
                        //TODO Sort from leftRoot then rightRoot once
                        if (newMergedHead == null) {
                            newMergedHead = leftListNode;
                            currentRoot = newMergedHead;
                            leftListNode = leftListNode.next;

                            tempNode = rightListNode;
                            rightListNode.next = currentRoot;
                            currentRoot = rightListNode;
                            rightListNode = tempNode.next;
                        } else {
                            tempNode = leftListNode;
                            leftListNode.next = currentRoot;
                            currentRoot = leftListNode;
                            leftListNode = tempNode.next;

                            tempNode = rightListNode;
                            rightListNode.next = currentRoot;
                            currentRoot = rightListNode;
                            rightListNode = tempNode.next;
                        }
                    }
                }

            }
            assert newMergedHead != null;
            newMergedHead.next = null;

            return currentRoot;
        }


        private class Node {
            T item;
            Node next;

            Node(T i, Node n) {
                item = i;
                next = n;
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
}
