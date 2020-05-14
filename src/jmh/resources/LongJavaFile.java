import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
/**
 CODE FROM https://github.com/TheAlgorithms/Java
 USES ONLY FOR BENCHMARK
 */
public class LongJavaFileProject {

    static <T> boolean swap(T[] array, int idx, int idy) {
        T swap = array[idx];
        array[idx] = array[idy];
        array[idy] = swap;
        return true;
    }

    static <T extends Comparable<T>> boolean less(T v, T w) {
        return v.compareTo(w) < 0;
    }

    static void print(List<?> toPrint) {
        toPrint.stream()
                .map(Object::toString)
                .map(str -> str + " ")
                .forEach(System.out::print);

        System.out.println();
    }

    static void print(Object[] toPrint) {
        System.out.println(Arrays.toString(toPrint));
    }

    static <T extends Comparable<T>> void flip(T[] array, int left, int right) {
        while (left <= right) {
            swap(array, left++, right--);
        }
    }
    private static final Random random = new Random();


    private static class BogoSort {

        private static final Random random = new Random();


        private static <T extends Comparable<T>> boolean isSorted(T[] array) {
            for (int i = 0; i < array.length - 1; i++) {
                if (less(array[i + 1], array[i])) return false;
            }
            return true;
        }

        // Randomly shuffles the array
        private static <T> void nextPermutation(T[] array) {
            int length = array.length;

            for (int i = 0; i < array.length; i++) {
                int randomIndex = i + random.nextInt(length - i);
                swap(array, randomIndex, i);
            }
        }

        public <T extends Comparable<T>> T[] sort(T[] array) {
            while (!isSorted(array)) {
                nextPermutation(array);
            }
            return array;
        }

        // Driver Program
        public static void main(String[] args) {
            // Integer Input
            Integer[] integers = {4, 23, 6, 78, 1, 54, 231, 9, 12};

            BogoSort bogoSort = new BogoSort();

            // print a sorted array
            print(bogoSort.sort(integers));

            // String Input
            String[] strings = {"c", "a", "e", "b", "d"};

            print(bogoSort.sort(strings));
        }
    }

    class BubbleSort {

        public <T extends Comparable<T>> T[] sort(T[] array) {
            for (int i = 0, size = array.length; i < size - 1; ++i) {
                boolean swapped = false;
                for (int j = 0; j < size - 1 - i; ++j) {
                    if (less(array[j], array[j + 1])) {
                        swap(array, j, j + 1);
                        swapped = true;
                    }
                }
                if (!swapped) {
                    break;
                }
            }
            return array;
        }
    }

    class CocktailShakerSort {

        public <T extends Comparable<T>> T[] sort(T[] array) {

            int length = array.length;
            int left = 0;
            int right = length - 1;
            int swappedLeft, swappedRight;
            while (left < right) {
                // front
                swappedRight = 0;
                for (int i = left; i < right; i++) {
                    if (less(array[i + 1], array[i])) {
                        swap(array, i, i + 1);
                        swappedRight = i;
                    }
                }
                // back
                right = swappedRight;
                swappedLeft = length - 1;
                for (int j = right; j > left; j--) {
                    if (less(array[j], array[j - 1])) {
                        swap(array, j - 1, j);
                        swappedLeft = j;
                    }
                }
                left = swappedLeft;
            }
            return array;

        }
    }

    class CombSort {

        // To find gap between elements
        private int nextGap(int gap) {
            // Shrink gap by Shrink factor
            gap = (gap * 10) / 13;
            return (gap < 1) ? 1 : gap;
        }

        public <T extends Comparable<T>> T[] sort(T[] arr) {
            int size = arr.length;

            // initialize gap
            int gap = size;

            // Initialize swapped as true to make sure that loop runs
            boolean swapped = true;

            // Keep running while gap is more than 1 and last iteration caused a swap
            while (gap != 1 || swapped) {
                // Find next gap
                gap = nextGap(gap);

                // Initialize swapped as false so that we can check if swap happened or not
                swapped = false;

                // Compare all elements with current gap
                for (int i = 0; i < size - gap; i++) {
                    if (less(arr[i + gap], arr[i])) {
                        // Swap arr[i] and arr[i+gap]
                        swapped = swap(arr, i, i + gap);
                    }
                }
            }
            return arr;
        }
    }

    public <T extends Comparable<T>> T[] sort(T[] unsorted) {
        return sort(Arrays.asList(unsorted)).toArray(unsorted);
    }

    public <T extends Comparable<T>> List<T> sort(List<T> list) {

        Map<T, Integer> frequency = new TreeMap<>();
        // The final output array
        List<T> sortedArray = new ArrayList<>(list.size());

        // Counting the frequency of @param array elements
        list.forEach(v -> frequency.put(v, frequency.getOrDefault(v, 0) + 1));

        // Filling the sortedArray
        for (Map.Entry<T, Integer> element : frequency.entrySet()) {
            for (int j = 0; j < element.getValue(); j++) {
                sortedArray.add(element.getKey());
            }
        }

        return sortedArray;
    }

    private static <T extends Comparable<T>> List<T> streamSort(List<T> list) {
        return list.stream()
                .collect(toMap(k -> k, v -> 1, (v1, v2) -> v1 + v2, TreeMap::new))
                .entrySet()
                .stream()
                .flatMap(entry -> IntStream.rangeClosed(1, entry.getValue()).mapToObj(t -> entry.getKey()))
                .collect(toList());
    }

    class CycleSort {

        public <T extends Comparable<T>> T[] sort(T[] arr) {
            int n = arr.length;

            // traverse array elements
            for (int j = 0; j <= n - 2; j++) {
                // initialize item as starting point
                T item = arr[j];

                // Find position where we put the item.
                int pos = j;
                for (int i = j + 1; i < n; i++)
                    if (less(arr[i], item)) pos++;

                // If item is already in correct position
                if (pos == j) continue;

                // ignore all duplicate elements
                while (item.compareTo(arr[pos]) == 0)
                    pos += 1;

                // put the item to it's right position
                if (pos != j) {
                    item = replace(arr, pos, item);
                }

                // Rotate rest of the cycle
                while (pos != j) {
                    pos = j;

                    // Find position where we put the element
                    for (int i = j + 1; i < n; i++)
                        if (less(arr[i], item)) {
                            pos += 1;
                        }


                    // ignore all duplicate elements
                    while (item.compareTo(arr[pos]) == 0)
                        pos += 1;

                    // put the item to it's right position
                    if (item != arr[pos]) {
                        item = replace(arr, pos, item);
                    }
                }
            }

            return arr;
        }

        private <T extends Comparable<T>> T replace(T[] arr, int pos, T item) {
            T temp = item;
            item = arr[pos];
            arr[pos] = temp;
            return item;
        }
    }

    class GnomeSort {

        public <T extends Comparable<T>> T[] sort(T[] arr) {
            int i = 1;
            int j = 2;
            while (i < arr.length) {
                if (less(arr[i - 1], arr[i])) i = j++;
                else {
                    swap(arr, i - 1, i);
                    if (--i == 0) {
                        i = j++;
                    }
                }
            }

            return null;
        }
    }

    public class HeapSort {


        private class Heap<T extends Comparable<T>> {

            private T[] heap;

            public Heap(T[] heap) {
                this.heap = heap;
            }

            private void heapSubtree(int rootIndex, int lastChild) {
                int leftIndex = rootIndex * 2 + 1;
                int rightIndex = rootIndex * 2 + 2;
                T root = heap[rootIndex];
                if (rightIndex <= lastChild) { // if has right and left children
                    T left = heap[leftIndex];
                    T right = heap[rightIndex];
                    if (less(left, right) && less(left, root)) {
                        swap(heap, leftIndex, rootIndex);
                        heapSubtree(leftIndex, lastChild);
                    } else if (less(right, root)) {
                        swap(heap, rightIndex, rootIndex);
                        heapSubtree(rightIndex, lastChild);
                    }
                } else if (leftIndex <= lastChild) { // if no right child, but has left child
                    T left = heap[leftIndex];
                    if (less(left, root)) {
                        swap(heap, leftIndex, rootIndex);
                        heapSubtree(leftIndex, lastChild);
                    }
                }
            }


            private void makeMinHeap(int root) {
                int leftIndex = root * 2 + 1;
                int rightIndex = root * 2 + 2;
                boolean hasLeftChild = leftIndex < heap.length;
                boolean hasRightChild = rightIndex < heap.length;
                if (hasRightChild) { //if has left and right
                    makeMinHeap(leftIndex);
                    makeMinHeap(rightIndex);
                    heapSubtree(root, heap.length - 1);
                } else if (hasLeftChild) {
                    heapSubtree(root, heap.length - 1);
                }
            }

            private T getRoot(int size) {
                swap(heap, 0, size);
                heapSubtree(0, size - 1);
                return heap[size]; // return old root
            }


        }

        public <T extends Comparable<T>> T[] sort(T[] unsorted) {
            return sort(Arrays.asList(unsorted)).toArray(unsorted);
        }

        public <T extends Comparable<T>> List<T> sort(List<T> unsorted) {
            int size = unsorted.size();

            @SuppressWarnings("unchecked")
            Heap<T> heap = new Heap<>(unsorted.toArray((T[]) new Comparable[unsorted.size()]));

            heap.makeMinHeap(0); // make min heap using index 0 as root.
            List<T> sorted = new ArrayList<>(size);
            while (size > 0) {
                T min = heap.getRoot(--size);
                sorted.add(min);
            }

            return sorted;
        }
    }

    class MergeSort {

        public <T extends Comparable<T>> T[] sort(T[] unsorted) {
            doSort(unsorted, 0, unsorted.length - 1);
            return unsorted;
        }

        private <T extends Comparable<T>> void doSort(T[] arr, int left, int right) {
            if (left < right) {
                int mid = left + (right - left) / 2;
                doSort(arr, left, mid);
                doSort(arr, mid + 1, right);
                merge(arr, left, mid, right);
            }

        }

        private <T extends Comparable<T>> void merge(T[] arr, int left, int mid, int right) {
            int length = right - left + 1;
            T[] temp = (T[]) new Comparable[length];
            int i = left;
            int j = mid + 1;
            int k = 0;

            while (i <= mid && j <= right) {
                if (arr[i].compareTo(arr[j]) <= 0) {
                    temp[k++] = arr[i++];
                } else {
                    temp[k++] = arr[j++];
                }
            }

            while (i <= mid) {
                temp[k++] = arr[i++];
            }

            while (j <= right) {
                temp[k++] = arr[j++];
            }

            System.arraycopy(temp, 0, arr, left, length);
        }
    }

    class QuickSort {

        public <T extends Comparable<T>> T[] sort(T[] array) {
            doSort(array, 0, array.length - 1);
            return array;
        }

        private <T extends Comparable<T>> void doSort(T[] array, int left, int right) {
            if (left < right) {
                int pivot = randomPartition(array, left, right);
                doSort(array, left, pivot - 1);
                doSort(array, pivot, right);
            }
        }

        private <T extends Comparable<T>> int randomPartition(T[] array, int left, int right) {
            int randomIndex = left + (int) (Math.random() * (right - left + 1));
            swap(array, randomIndex, right);
            return partition(array, left, right);
        }

        private <T extends Comparable<T>> int partition(T[] array, int left, int right) {
            int mid = (left + right) / 2;
            T pivot = array[mid];

            while (left <= right) {
                while (less(array[left], pivot)) {
                    ++left;
                }
                while (less(pivot, array[right])) {
                    --right;
                }
                if (left <= right) {
                    swap(array, left, right);
                    ++left;
                    --right;
                }
            }
            return left;
        }
    }

    class RadixSort {

        private int getMax(int[] arr, int n) {
            int mx = arr[0];
            for (int i = 1; i < n; i++)
                if (arr[i] > mx)
                    mx = arr[i];
            return mx;
        }

        private void countSort(int[] arr, int n, int exp) {
            int[] output = new int[n];
            int i;
            int[] count = new int[10];
            Arrays.fill(count, 0);

            for (i = 0; i < n; i++)
                count[(arr[i] / exp) % 10]++;

            for (i = 1; i < 10; i++)
                count[i] += count[i - 1];

            for (i = n - 1; i >= 0; i--) {
                output[count[(arr[i] / exp) % 10] - 1] = arr[i];
                count[(arr[i] / exp) % 10]--;
            }

            for (i = 0; i < n; i++)
                arr[i] = output[i];
        }

        private void radixsort(int[] arr, int n) {

            int m = getMax(arr, n);


            for (int exp = 1; m / exp > 0; exp *= 10)
                countSort(arr, n, exp);
        }


        void print(int[] arr, int n) {
            for (int i = 0; i < n; i++)
                System.out.print(arr[i] + " ");
        }
    }

    public class AnyBaseToAnyBase {

        static final int MINIMUM_BASE = 2;
        static final int MAXIMUM_BASE = 36;

        public void main(String[] args) {
            Scanner in = new Scanner(System.in);
            String n;
            int b1, b2;
            while (true) {
                try {
                    System.out.print("Enter number: ");
                    n = in.next();
                    System.out.print("Enter beginning base (between " + MINIMUM_BASE + " and " + MAXIMUM_BASE + "): ");
                    b1 = in.nextInt();
                    if (b1 > MAXIMUM_BASE || b1 < MINIMUM_BASE) {
                        System.out.println("Invalid base!");
                        continue;
                    }
                    if (!validForBase(n, b1)) {
                        System.out.println("The number is invalid for this base!");
                        continue;
                    }
                    System.out.print("Enter end base (between " + MINIMUM_BASE + " and " + MAXIMUM_BASE + "): ");
                    b2 = in.nextInt();
                    if (b2 > MAXIMUM_BASE || b2 < MINIMUM_BASE) {
                        System.out.println("Invalid base!");
                        continue;
                    }
                    break;
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input.");
                    in.next();
                }
            }
            System.out.println(base2base(n, b1, b2));
            in.close();
        }

        public boolean validForBase(String n, int base) {
            char[] validDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E',
                    'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
                    'W', 'X', 'Y', 'Z'};
            // digitsForBase contains all the valid digits for the base given
            char[] digitsForBase = Arrays.copyOfRange(validDigits, 0, base);

            // Convert character array into set for convenience of contains() method
            HashSet<Character> digitsList = new HashSet<>();
            for (int i = 0; i < digitsForBase.length; i++)
                digitsList.add(digitsForBase[i]);

            // Check that every digit in n is within the list of valid digits for that base.
            for (char c : n.toCharArray())
                if (!digitsList.contains(c))
                    return false;

            return true;
        }

        public String base2base(String n, int b1, int b2) {
            int decimalValue = 0, charB2;
            char charB1;
            String output = "";
            // Go through every character of n
            for (int i = 0; i < n.length(); i++) {
                // store the character in charB1
                charB1 = n.charAt(i);
                // if it is a non-number, convert it to a decimal value >9 and store it in charB2
                if (charB1 >= 'A' && charB1 <= 'Z')
                    charB2 = 10 + (charB1 - 'A');
                    // Else, store the integer value in charB2
                else
                    charB2 = charB1 - '0';
                // Convert the digit to decimal and add it to the
                // decimalValue of n
                decimalValue = decimalValue * b1 + charB2;
            }
            while (decimalValue != 0) {
                // If the remainder is a digit < 10, simply add it to
                // the left side of the new number.
                if (decimalValue % b2 < 10)
                    output = Integer.toString(decimalValue % b2) + output;
                else
                    output = (char) ((decimalValue % b2) + 55) + output;
                // Divide by the new base again
                decimalValue /= b2;
            }
            return output;
        }
    }

    public class RomanToInteger {

        private Map<Character, Integer> map = new HashMap<Character, Integer>() {
            /**
             *
             */
            private  final long serialVersionUID = 87605733047260530L;

            {
                put('I', 1);
                put('V', 5);
                put('X', 10);
                put('L', 50);
                put('C', 100);
                put('D', 500);
                put('M', 1000);
            }
        };

        public int romanToInt(String A) {

            char prev = ' ';

            int sum = 0;

            int newPrev = 0;
            for (int i = A.length() - 1; i >= 0; i--) {
                char c = A.charAt(i);

                if (prev != ' ') {
                    // checking current Number greater then previous or not
                    newPrev = map.get(prev) > newPrev ? map.get(prev) : newPrev;
                }

                int currentNum = map.get(c);

                // if current number greater then prev max previous then add
                if (currentNum >= newPrev) {
                    sum += currentNum;
                } else {
                    // subtract upcoming number until upcoming number not greater then prev max
                    sum -= currentNum;
                }

                prev = c;
            }

            return sum;
        }
    }

    public class Bag<Element> implements Iterable<Element> {

        private Node<Element> firstElement; // first element of the bag
        private int size; // size of bag

        private class Node<Element> {
            private Element content;
            private Node<Element> nextElement;
        }

        /**
         * Create an empty bag
         */
        public Bag() {
            firstElement = null;
            size = 0;
        }

        /**
         * @return true if this bag is empty, false otherwise
         */
        public boolean isEmpty() {
            return firstElement == null;
        }

        /**
         * @return the number of elements
         */
        public int size() {
            return size;
        }

        /**
         * @param element - the element to add
         */
        public void add(Element element) {
            Node<Element> oldfirst = firstElement;
            firstElement = new Node<>();
            firstElement.content = element;
            firstElement.nextElement = oldfirst;
            size++;
        }

        /**
         * Checks if the bag contains a specific element
         *
         * @param element which you want to look for
         * @return true if bag contains element, otherwise false
         */
        public boolean contains(Element element) {
            Iterator<Element> iterator = this.iterator();
            while (iterator.hasNext()) {
                if (iterator.next().equals(element)) {
                    return true;
                }
            }
            return false;
        }

        /**
         * @return an iterator that iterates over the elements in this bag in arbitrary order
         */
        public Iterator<Element> iterator() {
            return new ListIterator<>(firstElement);
        }

        @SuppressWarnings("hiding")
        private class ListIterator<Element> implements Iterator<Element> {
            private Node<Element> currentElement;

            public ListIterator(Node<Element> firstElement) {
                currentElement = firstElement;
            }

            public boolean hasNext() {
                return currentElement != null;
            }

            /**
             * remove is not allowed in a bag
             */
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

            public Element next() {
                if (!hasNext())
                    throw new NoSuchElementException();
                Element element = currentElement.content;
                currentElement = currentElement.nextElement;
                return element;
            }
        }
    }

    public class CircularBuffer {
        private char[] _buffer;
        public final int _buffer_size;
        private int _write_index = 0;
        private int _read_index = 0;
        private AtomicInteger _readable_data = new AtomicInteger(0);

        public CircularBuffer(int buffer_size) {
            if (!IsPowerOfTwo(buffer_size)) {
                throw new IllegalArgumentException();
            }
            this._buffer_size = buffer_size;
            _buffer = new char[buffer_size];
        }

        private boolean IsPowerOfTwo(int i) {
            return (i & (i - 1)) == 0;
        }

        private int getTrueIndex(int i) {
            return i % _buffer_size;
        }


        public Character readOutChar() {
            Character result = null;


            //if we have data to read
            if (_readable_data.get() > 0) {

                result = Character.valueOf(_buffer[getTrueIndex(_read_index)]);
                _readable_data.decrementAndGet();
                _read_index++;
            }

            return result;
        }

        public boolean writeToCharBuffer(char c) {
            boolean result = false;

            //if we can write to the buffer
            if (_readable_data.get() < _buffer_size) {
                //write to buffer
                _buffer[getTrueIndex(_write_index)] = c;
                _readable_data.incrementAndGet();
                _write_index++;
                result = true;
            }

            return result;
        }

        private class TestWriteWorker implements Runnable {
            String _alphabet = "abcdefghijklmnopqrstuvwxyz0123456789";
            Random _random = new Random();
            CircularBuffer _buffer;

            public TestWriteWorker(CircularBuffer cb) {
                this._buffer = cb;
            }

            private char getRandomChar() {
                return _alphabet.charAt(_random.nextInt(_alphabet.length()));
            }

            public void run() {
                while (!Thread.interrupted()) {
                    if (!_buffer.writeToCharBuffer(getRandomChar())) {
                        Thread.yield();
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            return;
                        }
                    }
                }
            }
        }

        private class TestReadWorker implements Runnable {
            CircularBuffer _buffer;

            public TestReadWorker(CircularBuffer cb) {
                this._buffer = cb;
            }

            public void run() {
                System.out.println("Printing Buffer:");
                while (!Thread.interrupted()) {
                    Character c = _buffer.readOutChar();
                    if (c != null) {
                        System.out.print(c.charValue());
                    } else {
                        Thread.yield();
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            System.out.println();
                            return;
                        }
                    }
                }
            }
        }
    }

    class BellmanFord {
        int vertex,edge;
        private Edge edges[];
        private int index=0;
        BellmanFord(int v,int e)
        {
            vertex=v;
            edge=e;
            edges=new Edge[e];
        }
        class Edge
        {
            int u,v;
            int w;
            Edge(int a,int b,int c)
            {
                u=a;
                v=b;
                w=c;
            }
        }

        void printPath(int p[],int i)
        {
            if(p[i]==-1)//Found the path back to parent
                return;
            printPath(p,p[i]);
            System.out.print(i+" ");
        }
        public void main(String args[])
        {
            BellmanFord obj=new BellmanFord(0,0);//Dummy object to call nonstatic variables
            obj.go();
        }
        public void go()//Interactive run for understanding the class first time. Assumes source vertex is 0 and shows distaance to all vertices
        {
            Scanner sc=new Scanner(System.in);//Grab scanner object for user input
            int i,v,e,u,ve,w,j,neg=0;
            System.out.println("Enter no. of vertices and edges please");
            v=sc.nextInt();
            e=sc.nextInt();
            Edge arr[]=new Edge[e];//Array of edges
            System.out.println("Input edges");
            for(i=0;i<e;i++)
            {
                u=sc.nextInt();
                ve=sc.nextInt();
                w=sc.nextInt();
                arr[i]=new Edge(u,ve,w);
            }
            int dist[]=new int[v];//Distance array for holding the finalized shortest path distance between source and all vertices
            int p[]=new int[v];//Parent array for holding the paths
            for(i=0;i<v;i++)
                dist[i]=Integer.MAX_VALUE;//Initializing distance values
            dist[0]=0;
            p[0]=-1;
            for(i=0;i<v-1;i++)
            {
                for(j=0;j<e;j++)
                {
                    if((int)dist[arr[j].u]!=Integer.MAX_VALUE&&dist[arr[j].v]>dist[arr[j].u]+arr[j].w)
                    {
                        dist[arr[j].v]=dist[arr[j].u]+arr[j].w;//Update
                        p[arr[j].v]=arr[j].u;
                    }
                }
            }
            //Final cycle for negative checking
            for(j=0;j<e;j++)
                if((int)dist[arr[j].u]!=Integer.MAX_VALUE&&dist[arr[j].v]>dist[arr[j].u]+arr[j].w)
                {
                    neg=1;
                    System.out.println("Negative cycle");
                    break;
                }
            if(neg==0)//Go ahead and show results of computaion
            {
                System.out.println("Distances are: ");
                for(i=0;i<v;i++)
                    System.out.println(i+" "+dist[i]);
                System.out.println("Path followed:");
                for(i=0;i<v;i++)
                {
                    System.out.print("0 ");
                    printPath(p,i);
                    System.out.println();
                }
            }
            sc.close();
        }

        public void show(int source,int end, Edge arr[]) {
            int i,j,v=vertex,e=edge,neg=0;
            double dist[]=new double[v];//Distance array for holding the finalized shortest path distance between source and all vertices
            int p[]=new int[v];//Parent array for holding the paths
            for(i=0;i<v;i++)
                dist[i]=Integer.MAX_VALUE;//Initializing distance values
            dist[source]=0;
            p[source]=-1;
            for(i=0;i<v-1;i++)
            {
                for(j=0;j<e;j++)
                {
                    if((int)dist[arr[j].u]!=Integer.MAX_VALUE&&dist[arr[j].v]>dist[arr[j].u]+arr[j].w)
                    {
                        dist[arr[j].v]=dist[arr[j].u]+arr[j].w;//Update
                        p[arr[j].v]=arr[j].u;
                    }
                }
            }
            //Final cycle for negative checking
            for(j=0;j<e;j++)
                if((int)dist[arr[j].u]!=Integer.MAX_VALUE&&dist[arr[j].v]>dist[arr[j].u]+arr[j].w)
                {
                    neg=1;
                    System.out.println("Negative cycle");
                    break;
                }
            if(neg==0)//Go ahead and show results of computaion
            {
                System.out.println("Distance is: "+dist[end]);
                System.out.println("Path followed:");
                System.out.print(source+" ");
                printPath(p,end);
                System.out.println();
            }
        }
        /**
         *@param x Source Vertex
         * @param y End vertex
         * @param z Weight
         */
        public void addEdge(int x,int y,int z)//Adds unidirectionl Edge
        {
            edges[index++]=new Edge(x,y,z);
        }
        public Edge[] getEdgeArray()
        {
            return edges;
        }
    }

    class Graph<E extends Comparable<E>> {

        class Node {
            E name;

            public Node(E name) {
                this.name = name;
            }
        }

        class Edge {
            Node startNode, endNode;

            public Edge(Node startNode, Node endNode) {
                this.startNode = startNode;
                this.endNode = endNode;
            }
        }

        ArrayList<Edge> edgeList;
        ArrayList<Node> nodeList;

        public Graph() {
            edgeList = new ArrayList<Edge>();
            nodeList = new ArrayList<Node>();
        }

        public void addEdge(E startNode, E endNode) {
            Node start = null, end = null;
            for (Node node : nodeList) {
                if (startNode.compareTo(node.name) == 0) {
                    start = node;
                } else if (endNode.compareTo(node.name) == 0) {
                    end = node;
                }
            }
            if (start == null) {
                start = new Node(startNode);
                nodeList.add(start);
            }
            if (end == null) {
                end = new Node(endNode);
                nodeList.add(end);
            }

            edgeList.add(new Edge(start, end));
        }

        public int countGraphs() {
            int count = 0;
            Set<Node> markedNodes = new HashSet<Node>();

            for (Node n : nodeList) {
                if (!markedNodes.contains(n)) {
                    markedNodes.add(n);
                    markedNodes.addAll(depthFirstSearch(n, new ArrayList<Node>()));
                    count++;
                }
            }

            return count;
        }

        public ArrayList<Node> depthFirstSearch(Node n, ArrayList<Node> visited) {
            visited.add(n);
            for (Edge e : edgeList) {
                if (e.startNode.equals(n) && !visited.contains(e.endNode)) {
                    depthFirstSearch(e.endNode, visited);
                }
            }
            return visited;
        }
    }

    class Cycle {

        private int nodes, edges;
        private int[][] adjacencyMatrix;
        private boolean[] visited;
        ArrayList<ArrayList<Integer>> cycles = new ArrayList<ArrayList<Integer>>();


        public Cycle() {
            Scanner in = new Scanner(System.in);
            System.out.print("Enter the no. of nodes: ");
            nodes = in.nextInt();
            System.out.print("Enter the no. of Edges: ");
            edges = in.nextInt();

            adjacencyMatrix = new int[nodes][nodes];
            visited = new boolean[nodes];

            for (int i = 0; i < nodes; i++) {
                visited[i] = false;
            }

            System.out.println("Enter the details of each edges <Start Node> <End Node>");

            for (int i = 0; i < edges; i++) {
                int start, end;
                start = in.nextInt();
                end = in.nextInt();
                adjacencyMatrix[start][end] = 1;
            }
            in.close();

        }

        public void start() {
            for (int i = 0; i < nodes; i++) {
                ArrayList<Integer> temp = new ArrayList<>();
                dfs(i, i, temp);
                for (int j = 0; j < nodes; j++) {
                    adjacencyMatrix[i][j] = 0;
                    adjacencyMatrix[j][i] = 0;
                }
            }
        }

        private void dfs(Integer start, Integer curr, ArrayList<Integer> temp) {
            temp.add(curr);
            visited[curr] = true;
            for (int i = 0; i < nodes; i++) {
                if (adjacencyMatrix[curr][i] == 1) {
                    if (i == start) {
                        cycles.add(new ArrayList<Integer>(temp));
                    } else {
                        if (!visited[i]) {
                            dfs(start, i, temp);
                        }
                    }
                }
            }

            if (temp.size() > 0) {
                temp.remove(temp.size() - 1);
            }
            visited[curr] = false;
        }

        public void printAll() {
            for (int i = 0; i < cycles.size(); i++) {
                for (int j = 0; j < cycles.get(i).size(); j++) {
                    System.out.print(cycles.get(i).get(j) + " -> ");
                }
                System.out.println(cycles.get(i).get(0));
                System.out.println();
            }

        }

    }

    public class Cycles {
        public void main(String[] args) {
            Cycle c = new Cycle();
            c.start();
            c.printAll();
        }
    }

    public class MatrixGraphs {

        public void main(String args[]) {
            AdjacencyMatrixGraph graph = new AdjacencyMatrixGraph(10);
            graph.addEdge(1, 2);
            graph.addEdge(1, 5);
            graph.addEdge(2, 5);
            graph.addEdge(1, 2);
            graph.addEdge(2, 3);
            graph.addEdge(3, 4);
            graph.addEdge(4, 1);
            graph.addEdge(2, 3);
            System.out.println(graph);
        }

    }

    class AdjacencyMatrixGraph {
        private int _numberOfVertices;
        private int _numberOfEdges;
        private int[][] _adjacency;

        static final int EDGE_EXIST = 1;
        static final int EDGE_NONE = 0;

        public AdjacencyMatrixGraph(int givenNumberOfVertices) {
            this.setNumberOfVertices(givenNumberOfVertices);
            this.setNumberOfEdges(0);
            this.setAdjacency(new int[givenNumberOfVertices][givenNumberOfVertices]);
            for (int i = 0; i < givenNumberOfVertices; i++) {
                for (int j = 0; j < givenNumberOfVertices; j++) {
                    this.adjacency()[i][j] = AdjacencyMatrixGraph.EDGE_NONE;
                }
            }
        }

        private void setNumberOfVertices(int newNumberOfVertices) {
            this._numberOfVertices = newNumberOfVertices;
        }

        public int numberOfVertices() {
            return this._numberOfVertices;
        }

        private void setNumberOfEdges(int newNumberOfEdges) {
            this._numberOfEdges = newNumberOfEdges;
        }

        public int numberOfEdges() {
            return this._numberOfEdges;
        }

        private void setAdjacency(int[][] newAdjacency) {
            this._adjacency = newAdjacency;
        }

        private int[][] adjacency() {
            return this._adjacency;
        }

        private boolean adjacencyOfEdgeDoesExist(int from, int to) {
            return (this.adjacency()[from][to] != AdjacencyMatrixGraph.EDGE_NONE);
        }

        public boolean vertexDoesExist(int aVertex) {
            if (aVertex >= 0 && aVertex < this.numberOfVertices()) {
                return true;
            } else {
                return false;
            }
        }

        public boolean edgeDoesExist(int from, int to) {
            if (this.vertexDoesExist(from) && this.vertexDoesExist(to)) {
                return (this.adjacencyOfEdgeDoesExist(from, to));
            }

            return false;
        }

        public boolean addEdge(int from, int to) {
            if (this.vertexDoesExist(from) && this.vertexDoesExist(to)) {
                if (!this.adjacencyOfEdgeDoesExist(from, to)) {
                    this.adjacency()[from][to] = AdjacencyMatrixGraph.EDGE_EXIST;
                    this.adjacency()[to][from] = AdjacencyMatrixGraph.EDGE_EXIST;
                    this.setNumberOfEdges(this.numberOfEdges() + 1);
                    return true;
                }
            }

            return false;
        }

        public boolean removeEdge(int from, int to) {
            if (!this.vertexDoesExist(from) || !this.vertexDoesExist(to)) {
                if (this.adjacencyOfEdgeDoesExist(from, to)) {
                    this.adjacency()[from][to] = AdjacencyMatrixGraph.EDGE_NONE;
                    this.adjacency()[to][from] = AdjacencyMatrixGraph.EDGE_NONE;
                    this.setNumberOfEdges(this.numberOfEdges() - 1);
                    return true;
                }
            }
            return false;
        }

        public String toString() {
            String s = new String();
            s = "    ";
            for (int i = 0; i < this.numberOfVertices(); i++) {
                s = s + String.valueOf(i) + " ";
            }
            s = s + " \n";

            for (int i = 0; i < this.numberOfVertices(); i++) {
                s = s + String.valueOf(i) + " : ";
                for (int j = 0; j < this.numberOfVertices(); j++) {
                    s = s + String.valueOf(this._adjacency[i][j]) + " ";
                }
                s = s + "\n";
            }
            return s;
        }
    }

    public class FordFulkerson {
        final static int INF = 987654321;
        // edges
        int V;
        int[][] capacity, flow;

        public void main(String[] args) {
            System.out.println("V : 6");
            V = 6;
            capacity = new int[V][V];

            capacity[0][1] = 12;
            capacity[0][3] = 13;
            capacity[1][2] = 10;
            capacity[2][3] = 13;
            capacity[2][4] = 3;
            capacity[2][5] = 15;
            capacity[3][2] = 7;
            capacity[3][4] = 15;
            capacity[4][5] = 17;

            System.out.println("Max capacity in networkFlow : " + networkFlow(0, 5));
        }

        private int networkFlow(int source, int sink) {
            flow = new int[V][V];
            int totalFlow = 0;
            while (true) {
                Vector<Integer> parent = new Vector<>(V);
                for (int i = 0; i < V; i++)
                    parent.add(-1);
                Queue<Integer> q = new LinkedList<>();
                parent.set(source, source);
                q.add(source);
                while (!q.isEmpty() && parent.get(sink) == -1) {
                    int here = q.peek();
                    q.poll();
                    for (int there = 0; there < V; ++there)
                        if (capacity[here][there] - flow[here][there] > 0 && parent.get(there) == -1) {
                            q.add(there);
                            parent.set(there, here);
                        }
                }
                if (parent.get(sink) == -1)
                    break;

                int amount = INF;
                String printer = "path : ";
                StringBuilder sb = new StringBuilder();
                for (int p = sink; p != source; p = parent.get(p)) {
                    amount = Math.min(capacity[parent.get(p)][p] - flow[parent.get(p)][p], amount);
                    sb.append(p + "-");
                }
                sb.append(source);
                for (int p = sink; p != source; p = parent.get(p)) {
                    flow[parent.get(p)][p] += amount;
                    flow[p][parent.get(p)] -= amount;
                }
                totalFlow += amount;
                printer += sb.reverse() + " / max flow : " + totalFlow;
                System.out.println(printer);
            }

            return totalFlow;
        }
    }

    class LongestCommonSubsequence {

        public String getLCS(String str1, String str2) {

            //At least one string is null
            if (str1 == null || str2 == null)
                return null;

            //At least one string is empty
            if (str1.length() == 0 || str2.length() == 0)
                return "";

            String[] arr1 = str1.split("");
            String[] arr2 = str2.split("");

            //lcsMatrix[i][j]  = LCS of first i elements of arr1 and first j characters of arr2
            int[][] lcsMatrix = new int[arr1.length + 1][arr2.length + 1];

            for (int i = 0; i < arr1.length + 1; i++)
                lcsMatrix[i][0] = 0;
            for (int j = 1; j < arr2.length + 1; j++)
                lcsMatrix[0][j] = 0;
            for (int i = 1; i < arr1.length + 1; i++) {
                for (int j = 1; j < arr2.length + 1; j++) {
                    if (arr1[i - 1].equals(arr2[j - 1])) {
                        lcsMatrix[i][j] = lcsMatrix[i - 1][j - 1] + 1;
                    } else {
                        lcsMatrix[i][j] = lcsMatrix[i - 1][j] > lcsMatrix[i][j - 1] ? lcsMatrix[i - 1][j] : lcsMatrix[i][j - 1];
                    }
                }
            }
            return lcsString(str1, str2, lcsMatrix);
        }

        public String lcsString(String str1, String str2, int[][] lcsMatrix) {
            StringBuilder lcs = new StringBuilder();
            int i = str1.length(),
                    j = str2.length();
            while (i > 0 && j > 0) {
                if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
                    lcs.append(str1.charAt(i - 1));
                    i--;
                    j--;
                } else if (lcsMatrix[i - 1][j] > lcsMatrix[i][j - 1]) {
                    i--;
                } else {
                    j--;
                }
            }
            return lcs.reverse().toString();
        }

        public void main(String[] args) {
            String str1 = "DSGSHSRGSRHTRD";
            String str2 = "DATRGAGTSHS";
            String lcs = getLCS(str1, str2);

            //Print LCS
            if (lcs != null) {
                System.out.println("String 1: " + str1);
                System.out.println("String 2: " + str2);
                System.out.println("LCS: " + lcs);
                System.out.println("LCS length: " + lcs.length());
            }
        }
    }

    public final class ClosestPair {


        /**
         * Number of points
         */
        int numberPoints = 0;
        /**
         * Input data, maximum 10000.
         */
        private Location[] array;
        /**
         * Minimum point coordinate.
         */
        Location point1 = null;
        /**
         * Minimum point coordinate.
         */
        Location point2 = null;
        /**
         * Minimum point length.
         */
        private  double minNum = Double.MAX_VALUE;

        private  int secondCount = 0;

        ClosestPair(int points) {
            numberPoints = points;
            array = new Location[numberPoints];
        }

        public class Location {

            double x = 0;
            double y = 0;

            Location(final double xpar, final double ypar) { //Save x, y coordinates
                this.x = xpar;
                this.y = ypar;
            }

        }

        public Location[] createLocation(int numberValues) {
            return new Location[numberValues];

        }

        public Location buildLocation(double x, double y) {
            return new Location(x, y);
        }

        public int xPartition(
                final Location[] a, final int first, final int last) {

            Location pivot = a[last]; // pivot
            int pIndex = last;
            int i = first - 1;
            Location temp; // Temporarily store value for position transformation
            for (int j = first; j <= last - 1; j++) {
                if (a[j].x <= pivot.x) { // Less than or less than pivot
                    i++;
                    temp = a[i]; // array[i] <-> array[j]
                    a[i] = a[j];
                    a[j] = temp;
                }
            }
            i++;
            temp = a[i]; // array[pivot] <-> array[i]
            a[i] = a[pIndex];
            a[pIndex] = temp;
            return i; // pivot index
        }

        public int yPartition(
                final Location[] a, final int first, final int last) {

            Location pivot = a[last]; // pivot
            int pIndex = last;
            int i = first - 1;
            Location temp; // Temporarily store value for position transformation
            for (int j = first; j <= last - 1; j++) {
                if (a[j].y <= pivot.y) { // Less than or less than pivot
                    i++;
                    temp = a[i]; // array[i] <-> array[j]
                    a[i] = a[j];
                    a[j] = temp;
                }
            }
            i++;
            temp = a[i]; // array[pivot] <-> array[i]
            a[i] = a[pIndex];
            a[pIndex] = temp;
            return i; // pivot index
        }

        public void xQuickSort(
                final Location[] a, final int first, final int last) {

            if (first < last) {
                int q = xPartition(a, first, last); // pivot
                xQuickSort(a, first, q - 1); // Left
                xQuickSort(a, q + 1, last); // Right
            }
        }

        public void yQuickSort(
                final Location[] a, final int first, final int last) {

            if (first < last) {
                int q = yPartition(a, first, last); // pivot
                yQuickSort(a, first, q - 1); // Left
                yQuickSort(a, q + 1, last); // Right
            }
        }
        public double closestPair(final Location[] a, final int indexNum) {

            Location[] divideArray = new Location[indexNum];
            System.arraycopy(a, 0, divideArray, 0, indexNum); // Copy previous array
            int totalNum = indexNum; // number of coordinates in the divideArray
            int divideX = indexNum / 2; // Intermediate value for divide
            Location[] leftArray = new Location[divideX]; //divide - left array
            //divide-right array
            Location[] rightArray = new Location[totalNum - divideX];
            if (indexNum <= 3) { // If the number of coordinates is 3 or less
                return bruteForce(divideArray);
            }
            //divide-left array
            System.arraycopy(divideArray, 0, leftArray, 0, divideX);
            //divide-right array
            System.arraycopy(
                    divideArray, divideX, rightArray, 0, totalNum - divideX);

            double minLeftArea = 0; //Minimum length of left array
            double minRightArea = 0; //Minimum length of right array
            double minValue = 0; //Minimum lengt

            minLeftArea = closestPair(leftArray, divideX); // recursive closestPair
            minRightArea = closestPair(rightArray, totalNum - divideX);
            // window size (= minimum length)
            minValue = Math.min(minLeftArea, minRightArea);

            // Create window.  Set the size for creating a window
            // and creating a new array for the coordinates in the window
            for (int i = 0; i < totalNum; i++) {
                double xGap = Math.abs(divideArray[divideX].x - divideArray[i].x);
                if (xGap < minValue) {
                    secondCount++; // size of the array
                } else {
                    if (divideArray[i].x > divideArray[divideX].x) {
                        break;
                    }
                }
            }
            // new array for coordinates in window
            Location[] firstWindow = new Location[secondCount];
            int k = 0;
            for (int i = 0; i < totalNum; i++) {
                double xGap = Math.abs(divideArray[divideX].x - divideArray[i].x);
                if (xGap < minValue) { // if it's inside a window
                    firstWindow[k] = divideArray[i]; // put in an array
                    k++;
                } else {
                    if (divideArray[i].x > divideArray[divideX].x) {
                        break;
                    }
                }
            }
            yQuickSort(firstWindow, 0, secondCount - 1); // Sort by y coordinates
            /* Coordinates in Window */
            double length = 0;
            // size comparison within window
            for (int i = 0; i < secondCount - 1; i++) {
                for (int j = (i + 1); j < secondCount; j++) {
                    double xGap = Math.abs(firstWindow[i].x - firstWindow[j].x);
                    double yGap = Math.abs(firstWindow[i].y - firstWindow[j].y);
                    if (yGap < minValue) {
                        length = Math.sqrt(Math.pow(xGap, 2) + Math.pow(yGap, 2));
                        // If measured distance is less than current min distance
                        if (length < minValue) {
                            // Change minimum distance to current distance
                            minValue = length;
                            // Conditional for registering final coordinate
                            if (length < minNum) {
                                minNum = length;
                                point1 = firstWindow[i];
                                point2 = firstWindow[j];
                            }
                        }
                    } else {
                        break;
                    }
                }
            }
            secondCount = 0;
            return minValue;
        }

        public double bruteForce(final Location[] arrayParam) {

            double minValue = Double.MAX_VALUE; // minimum distance
            double length = 0;
            double xGap = 0; // Difference between x coordinates
            double yGap = 0; // Difference between y coordinates
            double result = 0;

            if (arrayParam.length == 2) {
                // Difference between x coordinates
                xGap = (arrayParam[0].x - arrayParam[1].x);
                // Difference between y coordinates
                yGap = (arrayParam[0].y - arrayParam[1].y);
                // distance between coordinates
                length = Math.sqrt(Math.pow(xGap, 2) + Math.pow(yGap, 2));
                // Conditional statement for registering final coordinate
                if (length < minNum) {
                    minNum = length;

                }
                point1 = arrayParam[0];
                point2 = arrayParam[1];
                result = length;
            }
            if (arrayParam.length == 3) {
                for (int i = 0; i < arrayParam.length - 1; i++) {
                    for (int j = (i + 1); j < arrayParam.length; j++) {
                        // Difference between x coordinates
                        xGap = (arrayParam[i].x - arrayParam[j].x);
                        // Difference between y coordinates
                        yGap = (arrayParam[i].y - arrayParam[j].y);
                        // distance between coordinates
                        length =
                                Math.sqrt(Math.pow(xGap, 2) + Math.pow(yGap, 2));
                        // If measured distance is less than current min distance
                        if (length < minValue) {
                            // Change minimum distance to current distance
                            minValue = length;
                            if (length < minNum) {
                                // Registering final coordinate
                                minNum = length;
                                point1 = arrayParam[i];
                                point2 = arrayParam[j];
                            }
                        }
                    }
                }
                result = minValue;

            }
            return result; // If only one point returns 0.
        }

        public void main(final String[] args) {

            //Input data consists of one x-coordinate and one y-coordinate

            ClosestPair cp = new ClosestPair(12);
            cp.array[0] = cp.buildLocation(2, 3);
            cp.array[1] = cp.buildLocation(2, 16);
            cp.array[2] = cp.buildLocation(3, 9);
            cp.array[3] = cp.buildLocation(6, 3);
            cp.array[4] = cp.buildLocation(7, 7);
            cp.array[5] = cp.buildLocation(19, 4);
            cp.array[6] = cp.buildLocation(10, 11);
            cp.array[7] = cp.buildLocation(15, 2);
            cp.array[8] = cp.buildLocation(15, 19);
            cp.array[9] = cp.buildLocation(16, 11);
            cp.array[10] = cp.buildLocation(17, 13);
            cp.array[11] = cp.buildLocation(9, 12);

            System.out.println("Input data");
            System.out.println("Number of points: " + cp.array.length);
            for (int i = 0; i < cp.array.length; i++) {
                System.out.println("x: " + cp.array[i].x + ", y: " + cp.array[i].y);
            }

            cp.xQuickSort(cp.array, 0, cp.array.length - 1); // Sorting by x value

            double result; // minimum distance

            result = cp.closestPair(cp.array, cp.array.length);
            System.out.println("Output Data");
            System.out.println("(" + cp.point1.x + ", " + cp.point1.y + ")");
            System.out.println("(" + cp.point2.x + ", " + cp.point2.y + ")");
            System.out.println("Minimum Distance : " + result);

        }
    }

    public class SkylineAlgorithm {
        private ArrayList<Point> points;
        public SkylineAlgorithm() {
            points = new ArrayList<>();
        }

        public ArrayList<Point> getPoints() {
            return points;
        }

        public ArrayList<Point> produceSubSkyLines(ArrayList<Point> list) {

            // part where function exits flashback
            int size = list.size();
            if (size == 1) {
                return list;
            } else if (size == 2) {
                if (list.get(0).dominates(list.get(1))) {
                    list.remove(1);
                } else {
                    if (list.get(1).dominates(list.get(0))) {
                        list.remove(0);
                    }
                }
                return list;
            }

            // recursive part of the function
            ArrayList<Point> leftHalf = new ArrayList<>();
            ArrayList<Point> rightHalf = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                if (i < list.size() / 2) {
                    leftHalf.add(list.get(i));
                } else {
                    rightHalf.add(list.get(i));
                }
            }
            ArrayList<Point> leftSubSkyLine = produceSubSkyLines(leftHalf);
            ArrayList<Point> rightSubSkyLine = produceSubSkyLines(rightHalf);

            // skyline is produced
            return produceFinalSkyLine(leftSubSkyLine, rightSubSkyLine);
        }

        public ArrayList<Point> produceFinalSkyLine(ArrayList<Point> left, ArrayList<Point> right) {

            // dominated points of ArrayList left are removed
            for (int i = 0; i < left.size() - 1; i++) {
                if (left.get(i).x == left.get(i + 1).x && left.get(i).y > left.get(i + 1).y) {
                    left.remove(i);
                    i--;
                }
            }

            // minimum y-value is found
            int min = left.get(0).y;
            for (int i = 1; i < left.size(); i++) {
                if (min > left.get(i).y) {
                    min = left.get(i).y;
                    if (min == 1) {
                        i = left.size();
                    }
                }
            }

            // dominated points of ArrayList right are removed
            for (int i = 0; i < right.size(); i++) {
                if (right.get(i).y >= min) {
                    right.remove(i);
                    i--;
                }
            }

            // final skyline found and returned
            left.addAll(right);
            return left;
        }


        public class Point {
            private int x;
            private int y;

            public Point(int x, int y) {
                this.x = x;
                this.y = y;
            }

            /**
             * @return x, the x-value
             */
            public int getX() {
                return x;
            }

            /**
             * @return y, the y-value
             */
            public int getY() {
                return y;
            }

            public boolean dominates(Point p1) {
                // checks if p1 is dominated
                return (this.x < p1.x && this.y <= p1.y) || (this.x <= p1.x && this.y < p1.y);
            }
        }

        class XComparator implements Comparator<Point> {
            @Override
            public int compare(Point a, Point b) {
                return Integer.compare(a.x, b.x);
            }
        }
    }

    public class AES {

        private final int[] RCON = { 0x8d, 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80, 0x1b, 0x36, 0x6c, 0xd8,
                0xab, 0x4d, 0x9a, 0x2f, 0x5e, 0xbc, 0x63, 0xc6, 0x97, 0x35, 0x6a, 0xd4, 0xb3, 0x7d, 0xfa, 0xef, 0xc5, 0x91,
                0x39, 0x72, 0xe4, 0xd3, 0xbd, 0x61, 0xc2, 0x9f, 0x25, 0x4a, 0x94, 0x33, 0x66, 0xcc, 0x83, 0x1d, 0x3a, 0x74,
                0xe8, 0xcb, 0x8d, 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80, 0x1b, 0x36, 0x6c, 0xd8, 0xab, 0x4d, 0x9a,
                0x2f, 0x5e, 0xbc, 0x63, 0xc6, 0x97, 0x35, 0x6a, 0xd4, 0xb3, 0x7d, 0xfa, 0xef, 0xc5, 0x91, 0x39, 0x72, 0xe4,
                0xd3, 0xbd, 0x61, 0xc2, 0x9f, 0x25, 0x4a, 0x94, 0x33, 0x66, 0xcc, 0x83, 0x1d, 0x3a, 0x74, 0xe8, 0xcb, 0x8d,
                0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80, 0x1b, 0x36, 0x6c, 0xd8, 0xab, 0x4d, 0x9a, 0x2f, 0x5e, 0xbc,
                0x63, 0xc6, 0x97, 0x35, 0x6a, 0xd4, 0xb3, 0x7d, 0xfa, 0xef, 0xc5, 0x91, 0x39, 0x72, 0xe4, 0xd3, 0xbd, 0x61,
                0xc2, 0x9f, 0x25, 0x4a, 0x94, 0x33, 0x66, 0xcc, 0x83, 0x1d, 0x3a, 0x74, 0xe8, 0xcb, 0x8d, 0x01, 0x02, 0x04,
                0x08, 0x10, 0x20, 0x40, 0x80, 0x1b, 0x36, 0x6c, 0xd8, 0xab, 0x4d, 0x9a, 0x2f, 0x5e, 0xbc, 0x63, 0xc6, 0x97,
                0x35, 0x6a, 0xd4, 0xb3, 0x7d, 0xfa, 0xef, 0xc5, 0x91, 0x39, 0x72, 0xe4, 0xd3, 0xbd, 0x61, 0xc2, 0x9f, 0x25,
                0x4a, 0x94, 0x33, 0x66, 0xcc, 0x83, 0x1d, 0x3a, 0x74, 0xe8, 0xcb, 0x8d, 0x01, 0x02, 0x04, 0x08, 0x10, 0x20,
                0x40, 0x80, 0x1b, 0x36, 0x6c, 0xd8, 0xab, 0x4d, 0x9a, 0x2f, 0x5e, 0xbc, 0x63, 0xc6, 0x97, 0x35, 0x6a, 0xd4,
                0xb3, 0x7d, 0xfa, 0xef, 0xc5, 0x91, 0x39, 0x72, 0xe4, 0xd3, 0xbd, 0x61, 0xc2, 0x9f, 0x25, 0x4a, 0x94, 0x33,
                0x66, 0xcc, 0x83, 0x1d, 0x3a, 0x74, 0xe8, 0xcb, 0x8d };

        private final int[] SBOX = { 0x63, 0x7C, 0x77, 0x7B, 0xF2, 0x6B, 0x6F, 0xC5, 0x30, 0x01, 0x67, 0x2B, 0xFE,
                0xD7, 0xAB, 0x76, 0xCA, 0x82, 0xC9, 0x7D, 0xFA, 0x59, 0x47, 0xF0, 0xAD, 0xD4, 0xA2, 0xAF, 0x9C, 0xA4, 0x72,
                0xC0, 0xB7, 0xFD, 0x93, 0x26, 0x36, 0x3F, 0xF7, 0xCC, 0x34, 0xA5, 0xE5, 0xF1, 0x71, 0xD8, 0x31, 0x15, 0x04,
                0xC7, 0x23, 0xC3, 0x18, 0x96, 0x05, 0x9A, 0x07, 0x12, 0x80, 0xE2, 0xEB, 0x27, 0xB2, 0x75, 0x09, 0x83, 0x2C,
                0x1A, 0x1B, 0x6E, 0x5A, 0xA0, 0x52, 0x3B, 0xD6, 0xB3, 0x29, 0xE3, 0x2F, 0x84, 0x53, 0xD1, 0x00, 0xED, 0x20,
                0xFC, 0xB1, 0x5B, 0x6A, 0xCB, 0xBE, 0x39, 0x4A, 0x4C, 0x58, 0xCF, 0xD0, 0xEF, 0xAA, 0xFB, 0x43, 0x4D, 0x33,
                0x85, 0x45, 0xF9, 0x02, 0x7F, 0x50, 0x3C, 0x9F, 0xA8, 0x51, 0xA3, 0x40, 0x8F, 0x92, 0x9D, 0x38, 0xF5, 0xBC,
                0xB6, 0xDA, 0x21, 0x10, 0xFF, 0xF3, 0xD2, 0xCD, 0x0C, 0x13, 0xEC, 0x5F, 0x97, 0x44, 0x17, 0xC4, 0xA7, 0x7E,
                0x3D, 0x64, 0x5D, 0x19, 0x73, 0x60, 0x81, 0x4F, 0xDC, 0x22, 0x2A, 0x90, 0x88, 0x46, 0xEE, 0xB8, 0x14, 0xDE,
                0x5E, 0x0B, 0xDB, 0xE0, 0x32, 0x3A, 0x0A, 0x49, 0x06, 0x24, 0x5C, 0xC2, 0xD3, 0xAC, 0x62, 0x91, 0x95, 0xE4,
                0x79, 0xE7, 0xC8, 0x37, 0x6D, 0x8D, 0xD5, 0x4E, 0xA9, 0x6C, 0x56, 0xF4, 0xEA, 0x65, 0x7A, 0xAE, 0x08, 0xBA,
                0x78, 0x25, 0x2E, 0x1C, 0xA6, 0xB4, 0xC6, 0xE8, 0xDD, 0x74, 0x1F, 0x4B, 0xBD, 0x8B, 0x8A, 0x70, 0x3E, 0xB5,
                0x66, 0x48, 0x03, 0xF6, 0x0E, 0x61, 0x35, 0x57, 0xB9, 0x86, 0xC1, 0x1D, 0x9E, 0xE1, 0xF8, 0x98, 0x11, 0x69,
                0xD9, 0x8E, 0x94, 0x9B, 0x1E, 0x87, 0xE9, 0xCE, 0x55, 0x28, 0xDF, 0x8C, 0xA1, 0x89, 0x0D, 0xBF, 0xE6, 0x42,
                0x68, 0x41, 0x99, 0x2D, 0x0F, 0xB0, 0x54, 0xBB, 0x16 };

        private final int[] INVERSE_SBOX = { 0x52, 0x09, 0x6A, 0xD5, 0x30, 0x36, 0xA5, 0x38, 0xBF, 0x40, 0xA3, 0x9E,
                0x81, 0xF3, 0xD7, 0xFB, 0x7C, 0xE3, 0x39, 0x82, 0x9B, 0x2F, 0xFF, 0x87, 0x34, 0x8E, 0x43, 0x44, 0xC4, 0xDE,
                0xE9, 0xCB, 0x54, 0x7B, 0x94, 0x32, 0xA6, 0xC2, 0x23, 0x3D, 0xEE, 0x4C, 0x95, 0x0B, 0x42, 0xFA, 0xC3, 0x4E,
                0x08, 0x2E, 0xA1, 0x66, 0x28, 0xD9, 0x24, 0xB2, 0x76, 0x5B, 0xA2, 0x49, 0x6D, 0x8B, 0xD1, 0x25, 0x72, 0xF8,
                0xF6, 0x64, 0x86, 0x68, 0x98, 0x16, 0xD4, 0xA4, 0x5C, 0xCC, 0x5D, 0x65, 0xB6, 0x92, 0x6C, 0x70, 0x48, 0x50,
                0xFD, 0xED, 0xB9, 0xDA, 0x5E, 0x15, 0x46, 0x57, 0xA7, 0x8D, 0x9D, 0x84, 0x90, 0xD8, 0xAB, 0x00, 0x8C, 0xBC,
                0xD3, 0x0A, 0xF7, 0xE4, 0x58, 0x05, 0xB8, 0xB3, 0x45, 0x06, 0xD0, 0x2C, 0x1E, 0x8F, 0xCA, 0x3F, 0x0F, 0x02,
                0xC1, 0xAF, 0xBD, 0x03, 0x01, 0x13, 0x8A, 0x6B, 0x3A, 0x91, 0x11, 0x41, 0x4F, 0x67, 0xDC, 0xEA, 0x97, 0xF2,
                0xCF, 0xCE, 0xF0, 0xB4, 0xE6, 0x73, 0x96, 0xAC, 0x74, 0x22, 0xE7, 0xAD, 0x35, 0x85, 0xE2, 0xF9, 0x37, 0xE8,
                0x1C, 0x75, 0xDF, 0x6E, 0x47, 0xF1, 0x1A, 0x71, 0x1D, 0x29, 0xC5, 0x89, 0x6F, 0xB7, 0x62, 0x0E, 0xAA, 0x18,
                0xBE, 0x1B, 0xFC, 0x56, 0x3E, 0x4B, 0xC6, 0xD2, 0x79, 0x20, 0x9A, 0xDB, 0xC0, 0xFE, 0x78, 0xCD, 0x5A, 0xF4,
                0x1F, 0xDD, 0xA8, 0x33, 0x88, 0x07, 0xC7, 0x31, 0xB1, 0x12, 0x10, 0x59, 0x27, 0x80, 0xEC, 0x5F, 0x60, 0x51,
                0x7F, 0xA9, 0x19, 0xB5, 0x4A, 0x0D, 0x2D, 0xE5, 0x7A, 0x9F, 0x93, 0xC9, 0x9C, 0xEF, 0xA0, 0xE0, 0x3B, 0x4D,
                0xAE, 0x2A, 0xF5, 0xB0, 0xC8, 0xEB, 0xBB, 0x3C, 0x83, 0x53, 0x99, 0x61, 0x17, 0x2B, 0x04, 0x7E, 0xBA, 0x77,
                0xD6, 0x26, 0xE1, 0x69, 0x14, 0x63, 0x55, 0x21, 0x0C, 0x7D };

        private final int[] MULT2 = { 0x00, 0x02, 0x04, 0x06, 0x08, 0x0a, 0x0c, 0x0e, 0x10, 0x12, 0x14, 0x16, 0x18,
                0x1a, 0x1c, 0x1e, 0x20, 0x22, 0x24, 0x26, 0x28, 0x2a, 0x2c, 0x2e, 0x30, 0x32, 0x34, 0x36, 0x38, 0x3a, 0x3c,
                0x3e, 0x40, 0x42, 0x44, 0x46, 0x48, 0x4a, 0x4c, 0x4e, 0x50, 0x52, 0x54, 0x56, 0x58, 0x5a, 0x5c, 0x5e, 0x60,
                0x62, 0x64, 0x66, 0x68, 0x6a, 0x6c, 0x6e, 0x70, 0x72, 0x74, 0x76, 0x78, 0x7a, 0x7c, 0x7e, 0x80, 0x82, 0x84,
                0x86, 0x88, 0x8a, 0x8c, 0x8e, 0x90, 0x92, 0x94, 0x96, 0x98, 0x9a, 0x9c, 0x9e, 0xa0, 0xa2, 0xa4, 0xa6, 0xa8,
                0xaa, 0xac, 0xae, 0xb0, 0xb2, 0xb4, 0xb6, 0xb8, 0xba, 0xbc, 0xbe, 0xc0, 0xc2, 0xc4, 0xc6, 0xc8, 0xca, 0xcc,
                0xce, 0xd0, 0xd2, 0xd4, 0xd6, 0xd8, 0xda, 0xdc, 0xde, 0xe0, 0xe2, 0xe4, 0xe6, 0xe8, 0xea, 0xec, 0xee, 0xf0,
                0xf2, 0xf4, 0xf6, 0xf8, 0xfa, 0xfc, 0xfe, 0x1b, 0x19, 0x1f, 0x1d, 0x13, 0x11, 0x17, 0x15, 0x0b, 0x09, 0x0f,
                0x0d, 0x03, 0x01, 0x07, 0x05, 0x3b, 0x39, 0x3f, 0x3d, 0x33, 0x31, 0x37, 0x35, 0x2b, 0x29, 0x2f, 0x2d, 0x23,
                0x21, 0x27, 0x25, 0x5b, 0x59, 0x5f, 0x5d, 0x53, 0x51, 0x57, 0x55, 0x4b, 0x49, 0x4f, 0x4d, 0x43, 0x41, 0x47,
                0x45, 0x7b, 0x79, 0x7f, 0x7d, 0x73, 0x71, 0x77, 0x75, 0x6b, 0x69, 0x6f, 0x6d, 0x63, 0x61, 0x67, 0x65, 0x9b,
                0x99, 0x9f, 0x9d, 0x93, 0x91, 0x97, 0x95, 0x8b, 0x89, 0x8f, 0x8d, 0x83, 0x81, 0x87, 0x85, 0xbb, 0xb9, 0xbf,
                0xbd, 0xb3, 0xb1, 0xb7, 0xb5, 0xab, 0xa9, 0xaf, 0xad, 0xa3, 0xa1, 0xa7, 0xa5, 0xdb, 0xd9, 0xdf, 0xdd, 0xd3,
                0xd1, 0xd7, 0xd5, 0xcb, 0xc9, 0xcf, 0xcd, 0xc3, 0xc1, 0xc7, 0xc5, 0xfb, 0xf9, 0xff, 0xfd, 0xf3, 0xf1, 0xf7,
                0xf5, 0xeb, 0xe9, 0xef, 0xed, 0xe3, 0xe1, 0xe7, 0xe5 };

        private final int[] MULT3 = { 0x00, 0x03, 0x06, 0x05, 0x0c, 0x0f, 0x0a, 0x09, 0x18, 0x1b, 0x1e, 0x1d, 0x14,
                0x17, 0x12, 0x11, 0x30, 0x33, 0x36, 0x35, 0x3c, 0x3f, 0x3a, 0x39, 0x28, 0x2b, 0x2e, 0x2d, 0x24, 0x27, 0x22,
                0x21, 0x60, 0x63, 0x66, 0x65, 0x6c, 0x6f, 0x6a, 0x69, 0x78, 0x7b, 0x7e, 0x7d, 0x74, 0x77, 0x72, 0x71, 0x50,
                0x53, 0x56, 0x55, 0x5c, 0x5f, 0x5a, 0x59, 0x48, 0x4b, 0x4e, 0x4d, 0x44, 0x47, 0x42, 0x41, 0xc0, 0xc3, 0xc6,
                0xc5, 0xcc, 0xcf, 0xca, 0xc9, 0xd8, 0xdb, 0xde, 0xdd, 0xd4, 0xd7, 0xd2, 0xd1, 0xf0, 0xf3, 0xf6, 0xf5, 0xfc,
                0xff, 0xfa, 0xf9, 0xe8, 0xeb, 0xee, 0xed, 0xe4, 0xe7, 0xe2, 0xe1, 0xa0, 0xa3, 0xa6, 0xa5, 0xac, 0xaf, 0xaa,
                0xa9, 0xb8, 0xbb, 0xbe, 0xbd, 0xb4, 0xb7, 0xb2, 0xb1, 0x90, 0x93, 0x96, 0x95, 0x9c, 0x9f, 0x9a, 0x99, 0x88,
                0x8b, 0x8e, 0x8d, 0x84, 0x87, 0x82, 0x81, 0x9b, 0x98, 0x9d, 0x9e, 0x97, 0x94, 0x91, 0x92, 0x83, 0x80, 0x85,
                0x86, 0x8f, 0x8c, 0x89, 0x8a, 0xab, 0xa8, 0xad, 0xae, 0xa7, 0xa4, 0xa1, 0xa2, 0xb3, 0xb0, 0xb5, 0xb6, 0xbf,
                0xbc, 0xb9, 0xba, 0xfb, 0xf8, 0xfd, 0xfe, 0xf7, 0xf4, 0xf1, 0xf2, 0xe3, 0xe0, 0xe5, 0xe6, 0xef, 0xec, 0xe9,
                0xea, 0xcb, 0xc8, 0xcd, 0xce, 0xc7, 0xc4, 0xc1, 0xc2, 0xd3, 0xd0, 0xd5, 0xd6, 0xdf, 0xdc, 0xd9, 0xda, 0x5b,
                0x58, 0x5d, 0x5e, 0x57, 0x54, 0x51, 0x52, 0x43, 0x40, 0x45, 0x46, 0x4f, 0x4c, 0x49, 0x4a, 0x6b, 0x68, 0x6d,
                0x6e, 0x67, 0x64, 0x61, 0x62, 0x73, 0x70, 0x75, 0x76, 0x7f, 0x7c, 0x79, 0x7a, 0x3b, 0x38, 0x3d, 0x3e, 0x37,
                0x34, 0x31, 0x32, 0x23, 0x20, 0x25, 0x26, 0x2f, 0x2c, 0x29, 0x2a, 0x0b, 0x08, 0x0d, 0x0e, 0x07, 0x04, 0x01,
                0x02, 0x13, 0x10, 0x15, 0x16, 0x1f, 0x1c, 0x19, 0x1a };

        private final int[] MULT9 = { 0x00, 0x09, 0x12, 0x1b, 0x24, 0x2d, 0x36, 0x3f, 0x48, 0x41, 0x5a, 0x53, 0x6c,
                0x65, 0x7e, 0x77, 0x90, 0x99, 0x82, 0x8b, 0xb4, 0xbd, 0xa6, 0xaf, 0xd8, 0xd1, 0xca, 0xc3, 0xfc, 0xf5, 0xee,
                0xe7, 0x3b, 0x32, 0x29, 0x20, 0x1f, 0x16, 0x0d, 0x04, 0x73, 0x7a, 0x61, 0x68, 0x57, 0x5e, 0x45, 0x4c, 0xab,
                0xa2, 0xb9, 0xb0, 0x8f, 0x86, 0x9d, 0x94, 0xe3, 0xea, 0xf1, 0xf8, 0xc7, 0xce, 0xd5, 0xdc, 0x76, 0x7f, 0x64,
                0x6d, 0x52, 0x5b, 0x40, 0x49, 0x3e, 0x37, 0x2c, 0x25, 0x1a, 0x13, 0x08, 0x01, 0xe6, 0xef, 0xf4, 0xfd, 0xc2,
                0xcb, 0xd0, 0xd9, 0xae, 0xa7, 0xbc, 0xb5, 0x8a, 0x83, 0x98, 0x91, 0x4d, 0x44, 0x5f, 0x56, 0x69, 0x60, 0x7b,
                0x72, 0x05, 0x0c, 0x17, 0x1e, 0x21, 0x28, 0x33, 0x3a, 0xdd, 0xd4, 0xcf, 0xc6, 0xf9, 0xf0, 0xeb, 0xe2, 0x95,
                0x9c, 0x87, 0x8e, 0xb1, 0xb8, 0xa3, 0xaa, 0xec, 0xe5, 0xfe, 0xf7, 0xc8, 0xc1, 0xda, 0xd3, 0xa4, 0xad, 0xb6,
                0xbf, 0x80, 0x89, 0x92, 0x9b, 0x7c, 0x75, 0x6e, 0x67, 0x58, 0x51, 0x4a, 0x43, 0x34, 0x3d, 0x26, 0x2f, 0x10,
                0x19, 0x02, 0x0b, 0xd7, 0xde, 0xc5, 0xcc, 0xf3, 0xfa, 0xe1, 0xe8, 0x9f, 0x96, 0x8d, 0x84, 0xbb, 0xb2, 0xa9,
                0xa0, 0x47, 0x4e, 0x55, 0x5c, 0x63, 0x6a, 0x71, 0x78, 0x0f, 0x06, 0x1d, 0x14, 0x2b, 0x22, 0x39, 0x30, 0x9a,
                0x93, 0x88, 0x81, 0xbe, 0xb7, 0xac, 0xa5, 0xd2, 0xdb, 0xc0, 0xc9, 0xf6, 0xff, 0xe4, 0xed, 0x0a, 0x03, 0x18,
                0x11, 0x2e, 0x27, 0x3c, 0x35, 0x42, 0x4b, 0x50, 0x59, 0x66, 0x6f, 0x74, 0x7d, 0xa1, 0xa8, 0xb3, 0xba, 0x85,
                0x8c, 0x97, 0x9e, 0xe9, 0xe0, 0xfb, 0xf2, 0xcd, 0xc4, 0xdf, 0xd6, 0x31, 0x38, 0x23, 0x2a, 0x15, 0x1c, 0x07,
                0x0e, 0x79, 0x70, 0x6b, 0x62, 0x5d, 0x54, 0x4f, 0x46 };

        private final int[] MULT11 = { 0x00, 0x0b, 0x16, 0x1d, 0x2c, 0x27, 0x3a, 0x31, 0x58, 0x53, 0x4e, 0x45, 0x74,
                0x7f, 0x62, 0x69, 0xb0, 0xbb, 0xa6, 0xad, 0x9c, 0x97, 0x8a, 0x81, 0xe8, 0xe3, 0xfe, 0xf5, 0xc4, 0xcf, 0xd2,
                0xd9, 0x7b, 0x70, 0x6d, 0x66, 0x57, 0x5c, 0x41, 0x4a, 0x23, 0x28, 0x35, 0x3e, 0x0f, 0x04, 0x19, 0x12, 0xcb,
                0xc0, 0xdd, 0xd6, 0xe7, 0xec, 0xf1, 0xfa, 0x93, 0x98, 0x85, 0x8e, 0xbf, 0xb4, 0xa9, 0xa2, 0xf6, 0xfd, 0xe0,
                0xeb, 0xda, 0xd1, 0xcc, 0xc7, 0xae, 0xa5, 0xb8, 0xb3, 0x82, 0x89, 0x94, 0x9f, 0x46, 0x4d, 0x50, 0x5b, 0x6a,
                0x61, 0x7c, 0x77, 0x1e, 0x15, 0x08, 0x03, 0x32, 0x39, 0x24, 0x2f, 0x8d, 0x86, 0x9b, 0x90, 0xa1, 0xaa, 0xb7,
                0xbc, 0xd5, 0xde, 0xc3, 0xc8, 0xf9, 0xf2, 0xef, 0xe4, 0x3d, 0x36, 0x2b, 0x20, 0x11, 0x1a, 0x07, 0x0c, 0x65,
                0x6e, 0x73, 0x78, 0x49, 0x42, 0x5f, 0x54, 0xf7, 0xfc, 0xe1, 0xea, 0xdb, 0xd0, 0xcd, 0xc6, 0xaf, 0xa4, 0xb9,
                0xb2, 0x83, 0x88, 0x95, 0x9e, 0x47, 0x4c, 0x51, 0x5a, 0x6b, 0x60, 0x7d, 0x76, 0x1f, 0x14, 0x09, 0x02, 0x33,
                0x38, 0x25, 0x2e, 0x8c, 0x87, 0x9a, 0x91, 0xa0, 0xab, 0xb6, 0xbd, 0xd4, 0xdf, 0xc2, 0xc9, 0xf8, 0xf3, 0xee,
                0xe5, 0x3c, 0x37, 0x2a, 0x21, 0x10, 0x1b, 0x06, 0x0d, 0x64, 0x6f, 0x72, 0x79, 0x48, 0x43, 0x5e, 0x55, 0x01,
                0x0a, 0x17, 0x1c, 0x2d, 0x26, 0x3b, 0x30, 0x59, 0x52, 0x4f, 0x44, 0x75, 0x7e, 0x63, 0x68, 0xb1, 0xba, 0xa7,
                0xac, 0x9d, 0x96, 0x8b, 0x80, 0xe9, 0xe2, 0xff, 0xf4, 0xc5, 0xce, 0xd3, 0xd8, 0x7a, 0x71, 0x6c, 0x67, 0x56,
                0x5d, 0x40, 0x4b, 0x22, 0x29, 0x34, 0x3f, 0x0e, 0x05, 0x18, 0x13, 0xca, 0xc1, 0xdc, 0xd7, 0xe6, 0xed, 0xf0,
                0xfb, 0x92, 0x99, 0x84, 0x8f, 0xbe, 0xb5, 0xa8, 0xa3 };

        private final int[] MULT13 = { 0x00, 0x0d, 0x1a, 0x17, 0x34, 0x39, 0x2e, 0x23, 0x68, 0x65, 0x72, 0x7f, 0x5c,
                0x51, 0x46, 0x4b, 0xd0, 0xdd, 0xca, 0xc7, 0xe4, 0xe9, 0xfe, 0xf3, 0xb8, 0xb5, 0xa2, 0xaf, 0x8c, 0x81, 0x96,
                0x9b, 0xbb, 0xb6, 0xa1, 0xac, 0x8f, 0x82, 0x95, 0x98, 0xd3, 0xde, 0xc9, 0xc4, 0xe7, 0xea, 0xfd, 0xf0, 0x6b,
                0x66, 0x71, 0x7c, 0x5f, 0x52, 0x45, 0x48, 0x03, 0x0e, 0x19, 0x14, 0x37, 0x3a, 0x2d, 0x20, 0x6d, 0x60, 0x77,
                0x7a, 0x59, 0x54, 0x43, 0x4e, 0x05, 0x08, 0x1f, 0x12, 0x31, 0x3c, 0x2b, 0x26, 0xbd, 0xb0, 0xa7, 0xaa, 0x89,
                0x84, 0x93, 0x9e, 0xd5, 0xd8, 0xcf, 0xc2, 0xe1, 0xec, 0xfb, 0xf6, 0xd6, 0xdb, 0xcc, 0xc1, 0xe2, 0xef, 0xf8,
                0xf5, 0xbe, 0xb3, 0xa4, 0xa9, 0x8a, 0x87, 0x90, 0x9d, 0x06, 0x0b, 0x1c, 0x11, 0x32, 0x3f, 0x28, 0x25, 0x6e,
                0x63, 0x74, 0x79, 0x5a, 0x57, 0x40, 0x4d, 0xda, 0xd7, 0xc0, 0xcd, 0xee, 0xe3, 0xf4, 0xf9, 0xb2, 0xbf, 0xa8,
                0xa5, 0x86, 0x8b, 0x9c, 0x91, 0x0a, 0x07, 0x10, 0x1d, 0x3e, 0x33, 0x24, 0x29, 0x62, 0x6f, 0x78, 0x75, 0x56,
                0x5b, 0x4c, 0x41, 0x61, 0x6c, 0x7b, 0x76, 0x55, 0x58, 0x4f, 0x42, 0x09, 0x04, 0x13, 0x1e, 0x3d, 0x30, 0x27,
                0x2a, 0xb1, 0xbc, 0xab, 0xa6, 0x85, 0x88, 0x9f, 0x92, 0xd9, 0xd4, 0xc3, 0xce, 0xed, 0xe0, 0xf7, 0xfa, 0xb7,
                0xba, 0xad, 0xa0, 0x83, 0x8e, 0x99, 0x94, 0xdf, 0xd2, 0xc5, 0xc8, 0xeb, 0xe6, 0xf1, 0xfc, 0x67, 0x6a, 0x7d,
                0x70, 0x53, 0x5e, 0x49, 0x44, 0x0f, 0x02, 0x15, 0x18, 0x3b, 0x36, 0x21, 0x2c, 0x0c, 0x01, 0x16, 0x1b, 0x38,
                0x35, 0x22, 0x2f, 0x64, 0x69, 0x7e, 0x73, 0x50, 0x5d, 0x4a, 0x47, 0xdc, 0xd1, 0xc6, 0xcb, 0xe8, 0xe5, 0xf2,
                0xff, 0xb4, 0xb9, 0xae, 0xa3, 0x80, 0x8d, 0x9a, 0x97 };

        private final int[] MULT14 = { 0x00, 0x0e, 0x1c, 0x12, 0x38, 0x36, 0x24, 0x2a, 0x70, 0x7e, 0x6c, 0x62, 0x48,
                0x46, 0x54, 0x5a, 0xe0, 0xee, 0xfc, 0xf2, 0xd8, 0xd6, 0xc4, 0xca, 0x90, 0x9e, 0x8c, 0x82, 0xa8, 0xa6, 0xb4,
                0xba, 0xdb, 0xd5, 0xc7, 0xc9, 0xe3, 0xed, 0xff, 0xf1, 0xab, 0xa5, 0xb7, 0xb9, 0x93, 0x9d, 0x8f, 0x81, 0x3b,
                0x35, 0x27, 0x29, 0x03, 0x0d, 0x1f, 0x11, 0x4b, 0x45, 0x57, 0x59, 0x73, 0x7d, 0x6f, 0x61, 0xad, 0xa3, 0xb1,
                0xbf, 0x95, 0x9b, 0x89, 0x87, 0xdd, 0xd3, 0xc1, 0xcf, 0xe5, 0xeb, 0xf9, 0xf7, 0x4d, 0x43, 0x51, 0x5f, 0x75,
                0x7b, 0x69, 0x67, 0x3d, 0x33, 0x21, 0x2f, 0x05, 0x0b, 0x19, 0x17, 0x76, 0x78, 0x6a, 0x64, 0x4e, 0x40, 0x52,
                0x5c, 0x06, 0x08, 0x1a, 0x14, 0x3e, 0x30, 0x22, 0x2c, 0x96, 0x98, 0x8a, 0x84, 0xae, 0xa0, 0xb2, 0xbc, 0xe6,
                0xe8, 0xfa, 0xf4, 0xde, 0xd0, 0xc2, 0xcc, 0x41, 0x4f, 0x5d, 0x53, 0x79, 0x77, 0x65, 0x6b, 0x31, 0x3f, 0x2d,
                0x23, 0x09, 0x07, 0x15, 0x1b, 0xa1, 0xaf, 0xbd, 0xb3, 0x99, 0x97, 0x85, 0x8b, 0xd1, 0xdf, 0xcd, 0xc3, 0xe9,
                0xe7, 0xf5, 0xfb, 0x9a, 0x94, 0x86, 0x88, 0xa2, 0xac, 0xbe, 0xb0, 0xea, 0xe4, 0xf6, 0xf8, 0xd2, 0xdc, 0xce,
                0xc0, 0x7a, 0x74, 0x66, 0x68, 0x42, 0x4c, 0x5e, 0x50, 0x0a, 0x04, 0x16, 0x18, 0x32, 0x3c, 0x2e, 0x20, 0xec,
                0xe2, 0xf0, 0xfe, 0xd4, 0xda, 0xc8, 0xc6, 0x9c, 0x92, 0x80, 0x8e, 0xa4, 0xaa, 0xb8, 0xb6, 0x0c, 0x02, 0x10,
                0x1e, 0x34, 0x3a, 0x28, 0x26, 0x7c, 0x72, 0x60, 0x6e, 0x44, 0x4a, 0x58, 0x56, 0x37, 0x39, 0x2b, 0x25, 0x0f,
                0x01, 0x13, 0x1d, 0x47, 0x49, 0x5b, 0x55, 0x7f, 0x71, 0x63, 0x6d, 0xd7, 0xd9, 0xcb, 0xc5, 0xef, 0xe1, 0xf3,
                0xfd, 0xa7, 0xa9, 0xbb, 0xb5, 0x9f, 0x91, 0x83, 0x8d };

        public BigInteger scheduleCore(BigInteger t, int rconCounter) {
            String rBytes = t.toString(16);

            // Add zero padding
            while (rBytes.length() < 8) {
                rBytes = "0" + rBytes;
            }

            // rotate the first 16 bits to the back
            String rotatingBytes = rBytes.substring(0, 2);
            String fixedBytes = rBytes.substring(2);

            rBytes = fixedBytes + rotatingBytes;

            // apply S-Box to all 8-Bit Substrings
            for (int i = 0; i < 4; i++) {
                String currentByteBits = rBytes.substring(i * 2, (i + 1) * 2);

                int currentByte = Integer.parseInt(currentByteBits, 16);
                currentByte = SBOX[currentByte];

                // add the current RCON value to the first byte
                if (i == 0) {
                    currentByte = currentByte ^ RCON[rconCounter];
                }

                currentByteBits = Integer.toHexString(currentByte);

                // Add zero padding

                while (currentByteBits.length() < 2) {
                    currentByteBits = '0' + currentByteBits;
                }

                // replace bytes in original string
                rBytes = rBytes.substring(0, i * 2) + currentByteBits + rBytes.substring((i + 1) * 2);
            }

            // t = new BigInteger(rBytes, 16);
            // return t;
            return new BigInteger(rBytes, 16);
        }

        public BigInteger[] keyExpansion(BigInteger initialKey) {
            BigInteger[] roundKeys = { initialKey, new BigInteger("0"), new BigInteger("0"), new BigInteger("0"),
                    new BigInteger("0"), new BigInteger("0"), new BigInteger("0"), new BigInteger("0"), new BigInteger("0"),
                    new BigInteger("0"), new BigInteger("0"), };

            // initialize rcon iteration
            int rconCounter = 1;

            for (int i = 1; i < 11; i++) {

                // get the previous 32 bits the key
                BigInteger t = roundKeys[i - 1].remainder(new BigInteger("100000000", 16));

                // split previous key into 8-bit segments
                BigInteger[] prevKey = { roundKeys[i - 1].remainder(new BigInteger("100000000", 16)),
                        roundKeys[i - 1].remainder(new BigInteger("10000000000000000", 16))
                                .divide(new BigInteger("100000000", 16)),
                        roundKeys[i - 1].remainder(new BigInteger("1000000000000000000000000", 16))
                                .divide(new BigInteger("10000000000000000", 16)),
                        roundKeys[i - 1].divide(new BigInteger("1000000000000000000000000", 16)), };

                // run schedule core
                t = scheduleCore(t, rconCounter);
                rconCounter += 1;

                // Calculate partial round key
                BigInteger t0 = t.xor(prevKey[3]);
                BigInteger t1 = t0.xor(prevKey[2]);
                BigInteger t2 = t1.xor(prevKey[1]);
                BigInteger t3 = t2.xor(prevKey[0]);

                // Join round key segments
                t2 = t2.multiply(new BigInteger("100000000", 16));
                t1 = t1.multiply(new BigInteger("10000000000000000", 16));
                t0 = t0.multiply(new BigInteger("1000000000000000000000000", 16));
                roundKeys[i] = t0.add(t1).add(t2).add(t3);

            }
            return roundKeys;
        }

        public int[] splitBlockIntoCells(BigInteger block) {

            int[] cells = new int[16];
            String blockBits = block.toString(2);

            // Append leading 0 for full "128-bit" string
            while (blockBits.length() < 128) {
                blockBits = '0' + blockBits;
            }

            // split 128 to 8 bit cells
            for (int i = 0; i < cells.length; i++) {
                String cellBits = blockBits.substring(8 * i, 8 * (i + 1));
                cells[i] = Integer.parseInt(cellBits, 2);
            }

            return cells;
        }

        public BigInteger mergeCellsIntoBlock(int[] cells) {

            String blockBits = "";
            for (int i = 0; i < 16; i++) {
                String cellBits = Integer.toBinaryString(cells[i]);

                // Append leading 0 for full "8-bit" strings
                while (cellBits.length() < 8) {
                    cellBits = '0' + cellBits;
                }

                blockBits += cellBits;
            }

            return new BigInteger(blockBits, 2);
        }

        public BigInteger addRoundKey(BigInteger ciphertext, BigInteger key) {
            return ciphertext.xor(key);
        }

        public BigInteger subBytes(BigInteger ciphertext) {

            int[] cells = splitBlockIntoCells(ciphertext);

            for (int i = 0; i < 16; i++) {
                cells[i] = SBOX[cells[i]];
            }

            return mergeCellsIntoBlock(cells);
        }

        public BigInteger subBytesDec(BigInteger ciphertext) {

            int[] cells = splitBlockIntoCells(ciphertext);

            for (int i = 0; i < 16; i++) {
                cells[i] = INVERSE_SBOX[cells[i]];
            }

            return mergeCellsIntoBlock(cells);
        }

        public BigInteger shiftRows(BigInteger ciphertext) {
            int[] cells = splitBlockIntoCells(ciphertext);
            int[] output = new int[16];

            // do nothing in the first row
            output[0] = cells[0];
            output[4] = cells[4];
            output[8] = cells[8];
            output[12] = cells[12];

            // shift the second row backwards by one cell
            output[1] = cells[5];
            output[5] = cells[9];
            output[9] = cells[13];
            output[13] = cells[1];

            // shift the third row backwards by two cell
            output[2] = cells[10];
            output[6] = cells[14];
            output[10] = cells[2];
            output[14] = cells[6];

            // shift the forth row backwards by tree cell
            output[3] = cells[15];
            output[7] = cells[3];
            output[11] = cells[7];
            output[15] = cells[11];

            return mergeCellsIntoBlock(output);
        }

        public BigInteger shiftRowsDec(BigInteger ciphertext) {
            int[] cells = splitBlockIntoCells(ciphertext);
            int[] output = new int[16];

            // do nothing in the first row
            output[0] = cells[0];
            output[4] = cells[4];
            output[8] = cells[8];
            output[12] = cells[12];

            // shift the second row forwards by one cell
            output[1] = cells[13];
            output[5] = cells[1];
            output[9] = cells[5];
            output[13] = cells[9];

            // shift the third row forwards by two cell
            output[2] = cells[10];
            output[6] = cells[14];
            output[10] = cells[2];
            output[14] = cells[6];

            // shift the forth row forwards by tree cell
            output[3] = cells[7];
            output[7] = cells[11];
            output[11] = cells[15];
            output[15] = cells[3];

            return mergeCellsIntoBlock(output);
        }

        public BigInteger mixColumns(BigInteger ciphertext) {

            int[] cells = splitBlockIntoCells(ciphertext);
            int[] outputCells = new int[16];

            for (int i = 0; i < 4; i++) {
                int[] row = { cells[i * 4], cells[i * 4 + 1], cells[i * 4 + 2], cells[i * 4 + 3] };

                outputCells[i * 4] = MULT2[row[0]] ^ MULT3[row[1]] ^ row[2] ^ row[3];
                outputCells[i * 4 + 1] = row[0] ^ MULT2[row[1]] ^ MULT3[row[2]] ^ row[3];
                outputCells[i * 4 + 2] = row[0] ^ row[1] ^ MULT2[row[2]] ^ MULT3[row[3]];
                outputCells[i * 4 + 3] = MULT3[row[0]] ^ row[1] ^ row[2] ^ MULT2[row[3]];
            }
            return mergeCellsIntoBlock(outputCells);
        }

        public BigInteger mixColumnsDec(BigInteger ciphertext) {

            int[] cells = splitBlockIntoCells(ciphertext);
            int[] outputCells = new int[16];

            for (int i = 0; i < 4; i++) {
                int[] row = { cells[i * 4], cells[i * 4 + 1], cells[i * 4 + 2], cells[i * 4 + 3] };

                outputCells[i * 4] = MULT14[row[0]] ^ MULT11[row[1]] ^ MULT13[row[2]] ^ MULT9[row[3]];
                outputCells[i * 4 + 1] = MULT9[row[0]] ^ MULT14[row[1]] ^ MULT11[row[2]] ^ MULT13[row[3]];
                outputCells[i * 4 + 2] = MULT13[row[0]] ^ MULT9[row[1]] ^ MULT14[row[2]] ^ MULT11[row[3]];
                outputCells[i * 4 + 3] = MULT11[row[0]] ^ MULT13[row[1]] ^ MULT9[row[2]] ^ MULT14[row[3]];
            }
            return mergeCellsIntoBlock(outputCells);
        }

        public BigInteger encrypt(BigInteger plainText, BigInteger key) {
            BigInteger[] roundKeys = keyExpansion(key);

            // Initial round
            plainText = addRoundKey(plainText, roundKeys[0]);

            // Main rounds
            for (int i = 1; i < 10; i++) {
                plainText = subBytes(plainText);
                plainText = shiftRows(plainText);
                plainText = mixColumns(plainText);
                plainText = addRoundKey(plainText, roundKeys[i]);
            }

            // Final round
            plainText = subBytes(plainText);
            plainText = shiftRows(plainText);
            plainText = addRoundKey(plainText, roundKeys[10]);

            return plainText;
        }

        public BigInteger decrypt(BigInteger cipherText, BigInteger key) {

            BigInteger[] roundKeys = keyExpansion(key);

            // Invert final round
            cipherText = addRoundKey(cipherText, roundKeys[10]);
            cipherText = shiftRowsDec(cipherText);
            cipherText = subBytesDec(cipherText);

            // Invert main rounds
            for (int i = 9; i > 0; i--) {
                cipherText = addRoundKey(cipherText, roundKeys[i]);
                cipherText = mixColumnsDec(cipherText);
                cipherText = shiftRowsDec(cipherText);
                cipherText = subBytesDec(cipherText);
            }

            // Invert initial round
            cipherText = addRoundKey(cipherText, roundKeys[0]);

            return cipherText;
        }

        public void main(String[] args) {

            try (Scanner input = new Scanner(System.in)) {
                System.out.println("Enter (e) letter for encrpyt or (d) letter for decrypt :");
                char choice = input.nextLine().charAt(0);
                String in;
                switch (choice) {
                    case 'E':
                    case 'e':
                        System.out.println("Choose a plaintext block (128-Bit Integer in base 16):");
                        in = input.nextLine();
                        BigInteger plaintext = new BigInteger(in, 16);
                        System.out.println("Choose a Key (128-Bit Integer in base 16):");
                        in = input.nextLine();
                        BigInteger encryptionKey = new BigInteger(in, 16);
                        System.out.println("The encrypted message is: \n" + encrypt(plaintext, encryptionKey).toString(16));
                        break;
                    case 'D':
                    case 'd':
                        System.out.println("Enter your ciphertext block (128-Bit Integer in base 16):");
                        in = input.nextLine();
                        BigInteger ciphertext = new BigInteger(in, 16);
                        System.out.println("Choose a Key (128-Bit Integer in base 16):");
                        in = input.nextLine();
                        BigInteger decryptionKey = new BigInteger(in, 16);
                        System.out.println("The deciphered message is:\n" + decrypt(ciphertext, decryptionKey).toString(16));
                        break;
                    default:
                        System.out.println("** End **");
                }
            }
        }
    }

    public static class ColumnarTranspositionCipher {

        private static String keyword;
        private static Object[][] table;
        private static String abecedarium;
        public static final String ABECEDARIUM = "abcdefghijklmnopqrstuvwxyzABCDEFG"
                + "HIJKLMNOPQRSTUVWXYZ0123456789,.;:-@";
        private static final String ENCRYPTION_FIELD = "≈";
        private static final char ENCRYPTION_FIELD_CHAR = '≈';

        public static String encrpyter(String word, String keyword) {
            ColumnarTranspositionCipher.keyword = keyword;
            abecedariumBuilder(500);
            table = tableBuilder(word);
            Object[][] sortedTable = sortTable(table);
            String wordEncrypted = "";
            for (int i = 0; i < sortedTable[i].length; i++) {
                for (int j = 1; j < sortedTable.length; j++) {
                    wordEncrypted += sortedTable[j][i];
                }
            }
            return wordEncrypted;
        }

        public static String encrpyter(String word, String keyword, String abecedarium) {
            ColumnarTranspositionCipher.keyword = keyword;
            if (abecedarium != null) {
                ColumnarTranspositionCipher.abecedarium = abecedarium;
            } else {
                ColumnarTranspositionCipher.abecedarium = ABECEDARIUM;
            }
            table = tableBuilder(word);
            Object[][] sortedTable = sortTable(table);
            String wordEncrypted = "";
            for (int i = 0; i < sortedTable[0].length; i++) {
                for (int j = 1; j < sortedTable.length; j++) {
                    wordEncrypted += sortedTable[j][i];
                }
            }
            return wordEncrypted;
        }

        public static String decrypter() {
            String wordDecrypted = "";
            for (int i = 1; i < table.length; i++) {
                for (Object item : table[i]) {
                    wordDecrypted += item;
                }
            }
            return wordDecrypted.replaceAll(ENCRYPTION_FIELD, "");
        }

        private static Object[][] tableBuilder(String word) {
            Object[][] table = new Object[numberOfRows(word) + 1][keyword.length()];
            char[] wordInChards = word.toCharArray();
            //Fils in the respective numbers
            table[0] = findElements();
            int charElement = 0;
            for (int i = 1; i < table.length; i++) {
                for (int j = 0; j < table[i].length; j++) {
                    if (charElement < wordInChards.length) {
                        table[i][j] = wordInChards[charElement];
                        charElement++;
                    } else {
                        table[i][j] = ENCRYPTION_FIELD_CHAR;
                    }
                }
            }
            return table;
        }

        private static int numberOfRows(String word) {
            if ((double) word.length() / keyword.length() > word.length() / keyword.length()) {
                return (word.length() / keyword.length()) + 1;
            } else {
                return word.length() / keyword.length();
            }
        }

        private static Object[] findElements() {
            Object[] charValues = new Object[keyword.length()];
            for (int i = 0; i < charValues.length; i++) {
                int charValueIndex = abecedarium.indexOf(keyword.charAt(i));
                charValues[i] = charValueIndex > -1 ? charValueIndex : null;
            }
            return charValues;
        }

        private static Object[][] sortTable(Object[][] table) {
            Object[][] tableSorted = new Object[table.length][table[0].length];
            for (int i = 0; i < tableSorted.length; i++) {
                System.arraycopy(table[i], 0, tableSorted[i], 0, tableSorted[i].length);
            }
            for (int i = 0; i < tableSorted[0].length; i++) {
                for (int j = i + 1; j < tableSorted[0].length; j++) {
                    if ((int) tableSorted[0][i] > (int) table[0][j]) {
                        Object[] column = getColumn(tableSorted, tableSorted.length, i);
                        switchColumns(tableSorted, j, i, column);
                    }
                }
            }
            return tableSorted;
        }

        private static Object[] getColumn(Object[][] table, int rows, int column) {
            Object[] columnArray = new Object[rows];
            for (int i = 0; i < rows; i++) {
                columnArray[i] = table[i][column];
            }
            return columnArray;
        }

        private static void switchColumns(Object[][] table, int firstColumnIndex,
                                          int secondColumnIndex, Object[] columnToSwitch) {
            for (int i = 0; i < table.length; i++) {
                table[i][secondColumnIndex] = table[i][firstColumnIndex];
                table[i][firstColumnIndex] = columnToSwitch[i];
            }
        }

        private static void abecedariumBuilder(int value) {
            abecedarium = "";
            for (int i = 0; i < value; i++) {
                abecedarium += (char) i;
            }
        }

        private static void showTable() {
            for (Object[] table1 : table) {
                for (Object item : table1) {
                    System.out.print(item + " ");
                }
                System.out.println();
            }
        }

        public void main(String[] args) {
            String keywordForExample = "asd215";
            String wordBeingEncrypted = "This is a test of the Columnar Transposition Cipher";
            System.out.println("### Example of Columnar Transposition Cipher ###\n");
            System.out.println("Word being encryped ->>> " + wordBeingEncrypted);
            System.out.println("Word encrypted ->>> " + ColumnarTranspositionCipher
                    .encrpyter(wordBeingEncrypted, keywordForExample));
            System.out.println("Word decryped ->>> " + ColumnarTranspositionCipher
                    .decrypter());
            System.out.println("\n### Encrypted Table ###");
            showTable();
        }
    }

    public final class ClosestPairs {


        /**
         * Number of points
         */
        int numberPoints = 0;
        /**
         * Input data, maximum 10000.
         */
        private Location[] array;

        Location point1 = null;

        Location point2 = null;

        private double minNum = Double.MAX_VALUE;

        private int secondCount = 0;

        ClosestPairs(int points) {
            numberPoints = points;
            array = new Location[numberPoints];
        }


        public class Location {

            double x = 0;
            double y = 0;


            Location(final double xpar, final double ypar) { //Save x, y coordinates
                this.x = xpar;
                this.y = ypar;
            }

        }

        public Location[] createLocation(int numberValues) {
            return new Location[numberValues];

        }

        public Location buildLocation(double x, double y) {
            return new Location(x, y);
        }

        public int xPartition(
                final Location[] a, final int first, final int last) {

            Location pivot = a[last]; // pivot
            int pIndex = last;
            int i = first - 1;
            Location temp; // Temporarily store value for position transformation
            for (int j = first; j <= last - 1; j++) {
                if (a[j].x <= pivot.x) { // Less than or less than pivot
                    i++;
                    temp = a[i]; // array[i] <-> array[j]
                    a[i] = a[j];
                    a[j] = temp;
                }
            }
            i++;
            temp = a[i]; // array[pivot] <-> array[i]
            a[i] = a[pIndex];
            a[pIndex] = temp;
            return i; // pivot index
        }

        public int yPartition(
                final Location[] a, final int first, final int last) {

            Location pivot = a[last]; // pivot
            int pIndex = last;
            int i = first - 1;
            Location temp; // Temporarily store value for position transformation
            for (int j = first; j <= last - 1; j++) {
                if (a[j].y <= pivot.y) { // Less than or less than pivot
                    i++;
                    temp = a[i]; // array[i] <-> array[j]
                    a[i] = a[j];
                    a[j] = temp;
                }
            }
            i++;
            temp = a[i]; // array[pivot] <-> array[i]
            a[i] = a[pIndex];
            a[pIndex] = temp;
            return i; // pivot index
        }

        public void xQuickSort(
                final Location[] a, final int first, final int last) {

            if (first < last) {
                int q = xPartition(a, first, last); // pivot
                xQuickSort(a, first, q - 1); // Left
                xQuickSort(a, q + 1, last); // Right
            }
        }

        public void yQuickSort(
                final Location[] a, final int first, final int last) {

            if (first < last) {
                int q = yPartition(a, first, last); // pivot
                yQuickSort(a, first, q - 1); // Left
                yQuickSort(a, q + 1, last); // Right
            }
        }

        public double closestPair(final Location[] a, final int indexNum) {

            Location[] divideArray = new Location[indexNum];
            System.arraycopy(a, 0, divideArray, 0, indexNum); // Copy previous array
            int totalNum = indexNum; // number of coordinates in the divideArray
            int divideX = indexNum / 2; // Intermediate value for divide
            Location[] leftArray = new Location[divideX]; //divide - left array
            //divide-right array
            Location[] rightArray = new Location[totalNum - divideX];
            if (indexNum <= 3) { // If the number of coordinates is 3 or less
                return bruteForce(divideArray);
            }
            //divide-left array
            System.arraycopy(divideArray, 0, leftArray, 0, divideX);
            //divide-right array
            System.arraycopy(
                    divideArray, divideX, rightArray, 0, totalNum - divideX);

            double minLeftArea = 0; //Minimum length of left array
            double minRightArea = 0; //Minimum length of right array
            double minValue = 0; //Minimum lengt

            minLeftArea = closestPair(leftArray, divideX); // recursive closestPair
            minRightArea = closestPair(rightArray, totalNum - divideX);
            // window size (= minimum length)
            minValue = Math.min(minLeftArea, minRightArea);

            // Create window.  Set the size for creating a window
            // and creating a new array for the coordinates in the window
            for (int i = 0; i < totalNum; i++) {
                double xGap = Math.abs(divideArray[divideX].x - divideArray[i].x);
                if (xGap < minValue) {
                    secondCount++; // size of the array
                } else {
                    if (divideArray[i].x > divideArray[divideX].x) {
                        break;
                    }
                }
            }
            // new array for coordinates in window
            Location[] firstWindow = new Location[secondCount];
            int k = 0;
            for (int i = 0; i < totalNum; i++) {
                double xGap = Math.abs(divideArray[divideX].x - divideArray[i].x);
                if (xGap < minValue) { // if it's inside a window
                    firstWindow[k] = divideArray[i]; // put in an array
                    k++;
                } else {
                    if (divideArray[i].x > divideArray[divideX].x) {
                        break;
                    }
                }
            }
            yQuickSort(firstWindow, 0, secondCount - 1); // Sort by y coordinates
            /* Coordinates in Window */
            double length = 0;
            // size comparison within window
            for (int i = 0; i < secondCount - 1; i++) {
                for (int j = (i + 1); j < secondCount; j++) {
                    double xGap = Math.abs(firstWindow[i].x - firstWindow[j].x);
                    double yGap = Math.abs(firstWindow[i].y - firstWindow[j].y);
                    if (yGap < minValue) {
                        length = Math.sqrt(Math.pow(xGap, 2) + Math.pow(yGap, 2));
                        // If measured distance is less than current min distance
                        if (length < minValue) {
                            // Change minimum distance to current distance
                            minValue = length;
                            // Conditional for registering final coordinate
                            if (length < minNum) {
                                minNum = length;
                                point1 = firstWindow[i];
                                point2 = firstWindow[j];
                            }
                        }
                    } else {
                        break;
                    }
                }
            }
            secondCount = 0;
            return minValue;
        }

        public double bruteForce(final Location[] arrayParam) {

            double minValue = Double.MAX_VALUE; // minimum distance
            double length = 0;
            double xGap = 0; // Difference between x coordinates
            double yGap = 0; // Difference between y coordinates
            double result = 0;

            if (arrayParam.length == 2) {
                // Difference between x coordinates
                xGap = (arrayParam[0].x - arrayParam[1].x);
                // Difference between y coordinates
                yGap = (arrayParam[0].y - arrayParam[1].y);
                // distance between coordinates
                length = Math.sqrt(Math.pow(xGap, 2) + Math.pow(yGap, 2));
                // Conditional statement for registering final coordinate
                if (length < minNum) {
                    minNum = length;

                }
                point1 = arrayParam[0];
                point2 = arrayParam[1];
                result = length;
            }
            if (arrayParam.length == 3) {
                for (int i = 0; i < arrayParam.length - 1; i++) {
                    for (int j = (i + 1); j < arrayParam.length; j++) {
                        // Difference between x coordinates
                        xGap = (arrayParam[i].x - arrayParam[j].x);
                        // Difference between y coordinates
                        yGap = (arrayParam[i].y - arrayParam[j].y);
                        // distance between coordinates
                        length =
                                Math.sqrt(Math.pow(xGap, 2) + Math.pow(yGap, 2));
                        // If measured distance is less than current min distance
                        if (length < minValue) {
                            // Change minimum distance to current distance
                            minValue = length;
                            if (length < minNum) {
                                // Registering final coordinate
                                minNum = length;
                                point1 = arrayParam[i];
                                point2 = arrayParam[j];
                            }
                        }
                    }
                }
                result = minValue;

            }
            return result; // If only one point returns 0.
        }

        public void main(final String[] args) {

            //Input data consists of one x-coordinate and one y-coordinate

            ClosestPair cp = new ClosestPair(12);
            cp.array[0] = cp.buildLocation(2, 3);
            cp.array[1] = cp.buildLocation(2, 16);
            cp.array[2] = cp.buildLocation(3, 9);
            cp.array[3] = cp.buildLocation(6, 3);
            cp.array[4] = cp.buildLocation(7, 7);
            cp.array[5] = cp.buildLocation(19, 4);
            cp.array[6] = cp.buildLocation(10, 11);
            cp.array[7] = cp.buildLocation(15, 2);
            cp.array[8] = cp.buildLocation(15, 19);
            cp.array[9] = cp.buildLocation(16, 11);
            cp.array[10] = cp.buildLocation(17, 13);
            cp.array[11] = cp.buildLocation(9, 12);

            System.out.println("Input data");
            System.out.println("Number of points: " + cp.array.length);
            for (int i = 0; i < cp.array.length; i++) {
                System.out.println("x: " + cp.array[i].x + ", y: " + cp.array[i].y);
            }

            cp.xQuickSort(cp.array, 0, cp.array.length - 1); // Sorting by x value

            double result; // minimum distance

            result = cp.closestPair(cp.array, cp.array.length);
            // ClosestPair start
            // minimum distance coordinates and distance output
            System.out.println("Output Data");
            System.out.println("(" + cp.point1.x + ", " + cp.point1.y + ")");
            System.out.println("(" + cp.point2.x + ", " + cp.point2.y + ")");
            System.out.println("Minimum Distance : " + result);

        }
    }

    public class ColumnarTranspositionCiphers {

        private String keyword;
        private Object[][] table;
        private String abecedarium;
        public static final String ABECEDARIUM = "abcdefghijklmnopqrstuvwxyzABCDEFG"
                + "HIJKLMNOPQRSTUVWXYZ0123456789,.;:-@";
        private static final String ENCRYPTION_FIELD = "≈";
        private static final char ENCRYPTION_FIELD_CHAR = '≈';

        public String encrpyter(String word, String keyword) {
            ColumnarTranspositionCipher.keyword = keyword;
            abecedariumBuilder(500);
            table = tableBuilder(word);
            Object[][] sortedTable = sortTable(table);
            String wordEncrypted = "";
            for (int i = 0; i < sortedTable[i].length; i++) {
                for (int j = 1; j < sortedTable.length; j++) {
                    wordEncrypted += sortedTable[j][i];
                }
            }
            return wordEncrypted;
        }

        public String encrpyter(String word, String keyword,
                                       String abecedarium) {
            ColumnarTranspositionCipher.keyword = keyword;
            if (abecedarium != null) {
                ColumnarTranspositionCipher.abecedarium = abecedarium;
            } else {
                ColumnarTranspositionCipher.abecedarium = ABECEDARIUM;
            }
            table = tableBuilder(word);
            Object[][] sortedTable = sortTable(table);
            String wordEncrypted = "";
            for (int i = 0; i < sortedTable[0].length; i++) {
                for (int j = 1; j < sortedTable.length; j++) {
                    wordEncrypted += sortedTable[j][i];
                }
            }
            return wordEncrypted;
        }

        public String decrypter() {
            String wordDecrypted = "";
            for (int i = 1; i < table.length; i++) {
                for (Object item : table[i]) {
                    wordDecrypted += item;
                }
            }
            return wordDecrypted.replaceAll(ENCRYPTION_FIELD, "");
        }

        private Object[][] tableBuilder(String word) {
            Object[][] table = new Object[numberOfRows(word) + 1][keyword.length()];
            char[] wordInChards = word.toCharArray();
            //Fils in the respective numbers
            table[0] = findElements();
            int charElement = 0;
            for (int i = 1; i < table.length; i++) {
                for (int j = 0; j < table[i].length; j++) {
                    if (charElement < wordInChards.length) {
                        table[i][j] = wordInChards[charElement];
                        charElement++;
                    } else {
                        table[i][j] = ENCRYPTION_FIELD_CHAR;
                    }
                }
            }
            return table;
        }

        private int numberOfRows(String word) {
            if ((double) word.length() / keyword.length() > word.length() / keyword.length()) {
                return (word.length() / keyword.length()) + 1;
            } else {
                return word.length() / keyword.length();
            }
        }

        /**
         *
         * @return charValues
         */
        private Object[] findElements() {
            Object[] charValues = new Object[keyword.length()];
            for (int i = 0; i < charValues.length; i++) {
                int charValueIndex = abecedarium.indexOf(keyword.charAt(i));
                charValues[i] = charValueIndex > -1 ? charValueIndex : null;
            }
            return charValues;
        }

        private Object[][] sortTable(Object[][] table) {
            Object[][] tableSorted = new Object[table.length][table[0].length];
            for (int i = 0; i < tableSorted.length; i++) {
                System.arraycopy(table[i], 0, tableSorted[i], 0, tableSorted[i].length);
            }
            for (int i = 0; i < tableSorted[0].length; i++) {
                for (int j = i + 1; j < tableSorted[0].length; j++) {
                    if ((int) tableSorted[0][i] > (int) table[0][j]) {
                        Object[] column = getColumn(tableSorted, tableSorted.length, i);
                        switchColumns(tableSorted, j, i, column);
                    }
                }
            }
            return tableSorted;
        }

        private Object[] getColumn(Object[][] table, int rows, int column) {
            Object[] columnArray = new Object[rows];
            for (int i = 0; i < rows; i++) {
                columnArray[i] = table[i][column];
            }
            return columnArray;
        }

        private void switchColumns(Object[][] table, int firstColumnIndex,
                                          int secondColumnIndex, Object[] columnToSwitch) {
            for (int i = 0; i < table.length; i++) {
                table[i][secondColumnIndex] = table[i][firstColumnIndex];
                table[i][firstColumnIndex] = columnToSwitch[i];
            }
        }

        private void abecedariumBuilder(int value) {
            abecedarium = "";
            for (int i = 0; i < value; i++) {
                abecedarium += (char) i;
            }
        }

        private void showTable() {
            for (Object[] table1 : table) {
                for (Object item : table1) {
                    System.out.print(item + " ");
                }
                System.out.println();
            }
        }

        public void main(String[] args) {
            String keywordForExample = "asd215";
            String wordBeingEncrypted = "This is a test of the Columnar Transposition Cipher";
            System.out.println("### Example of Columnar Transposition Cipher ###\n");
            System.out.println("Word being encryped ->>> " + wordBeingEncrypted);
            System.out.println("Word encrypted ->>> " + ColumnarTranspositionCipher
                    .encrpyter(wordBeingEncrypted, keywordForExample));
            System.out.println("Word decryped ->>> " + ColumnarTranspositionCipher
                    .decrypter());
            System.out.println("\n### Encrypted Table ###");
            showTable();
        }
    }

    public class BinarySearchTree<T extends Comparable<T>> implements Collection<T>, Iterable<T> {
        private Node root = null;
        private int size = 0;

        /**
         * @return number of elements in the collection
         */
        @Override
        public int size() {
            return this.size;
        }

        /**
         * @return if collection is empty
         */
        @Override
        public boolean isEmpty() {
            return size == 0;
        }

        /**
         * @param element to check for contains
         * @return if the object contains in collection
         */
        @Override
        @SuppressWarnings("unchecked")
        public boolean contains(Object element) {
            return !isEmpty() && root.contains((T) element);
        }

        /**
         * @return tree iterator
         */
        @Override
        public Iterator<T> iterator() {
            return new BSTIterator();
        }

        /**
         * Method adding elements from the collection to array
         * @param array to add elements
         * @param <T1> parameter of array elements
         * @return array with elements from the collection
         */
        @Override
        @SuppressWarnings("unchecked")
        public <T1>T1[] toArray(T1[] array) {
            ArrayList<T1> result = new ArrayList<>();
            for (T tmp : this) {
                result.add((T1) tmp);
            }
            return result.toArray(array);
        }

        /**
         * @return elements of collection as array of Objects in increasing order
         */
        @Override
        public Object[] toArray() {
            return toArray(new Object[size]);
        }

        /**
         * Method adding element to the collection
         * @param value element to add
         * @return if the element will be added
         */
        @Override
        public boolean add(T value) {
            if (root == null) {
                root = new Node(value);
                ++size;
                return true;
            }
            return root.add(value);
        }

        /**
         * Method removing element from the collection
         * @param value to remove from collection
         * @return if the element will be removed
         */
        @Override
        @SuppressWarnings("unchecked")
        public boolean remove(Object value) {
            return !isEmpty() && root.remove((T) value);
        }

        /**
         * @param collection to check for contains
         * @return if Tree contains every element from the collection
         */
        @Override
        public boolean containsAll(Collection<?> collection) {
            boolean result = true;
            for (Object tmp : collection) {
                result &= contains(tmp);
            }
            return result;
        }

        /**
         * @param collection with elements to add
         * @return if all elements will be added
         */
        @Override
        public boolean addAll(Collection<? extends T> collection) {
            boolean result = true;
            for (T tmp : collection) {
                result &= add(tmp);
            }
            return result;
        }

        /**
         * @param collection with elements to remove
         * @return if all elements will be removed
         */
        @Override
        public boolean removeAll(Collection<?> collection) {
            boolean result = true;
            for (Object tmp : collection) {
                result &= remove(tmp);
            }
            return result;
        }

        /**
         * @param collection with elements to retain
         * @return if this collection will be changed
         */
        @Override
        public boolean retainAll(Collection<?> collection) {
            boolean result = false;
            for (Object tmp : collection) {
                if (contains(tmp)) {
                    remove(tmp);
                    result = true;
                }
            }
            return result;
        }

        /**
         * Clear the collection
         */
        @Override
        public void clear() {
            root = null;
            size = 0;
        }

        /**
         * @return String representation of the collection
         */
        @Override
        public String toString() {
            return isEmpty() ? "null" : root.toString();
        }

        /**
         * Methods gets all Nodes from current subtree to the List
         * @param node subtree to get Nodes
         * @param elements List to add elements
         * @return List with elements
         */
        private ArrayList<Node> getAll(Node node, ArrayList<Node> elements) {
            if (node.left != null) {
                getAll(node.left, elements);
            }
            elements.add(node);
            if (node.right != null) {
                getAll(node.right, elements);
            }
            return elements;
        }

        /** Class realizing the Binary Search Tree Iterator */
        private class BSTIterator implements Iterator<T> {
            private ArrayList<Node> elements = new ArrayList<>();

            private BSTIterator() {
                if (!isEmpty()) {
                    BinarySearchTree.this.getAll(root, elements);
                }
            }

            /**
             * @return if the next element exist
             */
            @Override
            public boolean hasNext() {
                return !elements.isEmpty() && treeContainsAtLeastOneElement();
            }

            /**
             * @return if the List contains at least one element from the tree
             */
            private boolean treeContainsAtLeastOneElement() {
                for (Node tmp : elements) {
                    if (BinarySearchTree.this.contains(tmp.value)) {
                        return true;
                    }
                }
                return false;
            }

            /**
             * @return value of next element, null if it does not exist
             */
            @Override
            public T next() {
                if (elements.isEmpty()) {
                    return null;
                }
                if (!root.contains(elements.get(0).value)) {
                    elements.remove(0);
                    return next();
                }
                return elements.remove(0).value;
            }
        }

        /**
         * Class describes Node of the Binary Tree
         */
        private class Node {
            private T value;
            private Node parent;
            private Node left = null;
            private Node right = null;

            private Node(T value) {
                this.value = value;
                this.parent = null;
            }

            private Node(T value, Node parent) {
                this.value = value;
                this.parent = parent;
            }

            /**
             * Method adds an element with a specified value
             * @param value of new element
             */
            private boolean add(T value) {
                if (value.compareTo(this.value) < 0) {
                    if (left == null) {
                        left = new Node(value, this);
                        ++size;
                        return true;
                    }
                    left.add(value);
                } else  if (value.compareTo(this.value) > 0) {
                    if (right == null) {
                        right = new Node(value, this);
                        ++size;
                        return true;
                    }
                    right.add(value);
                }
                return false;
            }

            /**
             * Method removes an element with a specified value
             * @param value of element to remove
             */
            private boolean remove(T value) {
                boolean result = false;
                if (value.compareTo(this.value) < 0) {
                    result = left != null && left.remove(value);
                } else if (value.compareTo(this.value) > 0) {
                    result = right != null && right.remove(value);
                } else {
                    --size;
                    this.remove();
                }
                return result;
            }

            /**
             * Method removing current Node
             */
            private void remove() {
                if (left != null && right != null) {
                    Node newNode = this.findMinimalInRightSubtree();
                    value = newNode.value;
                    changeNode(newNode);
                } else if (left != null) {
                    changeNode(left);
                } else if (right != null) {
                    changeNode(right);
                } else {
                    changeNode(null);
                }
            }

            /**
             * @return Node with minimum value in the subtree
             */
            private Node findMinimalInRightSubtree() {
                Node current = this.right;
                while (current.left != null) {
                    current = current.left;
                }
                return current;
            }

            private void changeNode(Node newNode) {
                if (newNode == null) {
                    if (parent == null) {
                        root = null;
                    } else {
                        if (equals(parent.left)) {
                            parent.left = null;
                        } else {
                            parent.right = null;
                        }
                    }
                    return;
                }
                value = newNode.value;
                if (newNode.equals(newNode.parent.left)) {
                    newNode.parent.left = newNode.left;
                } else {
                    newNode.parent.right = newNode.right;
                }
            }

            /**
             * @param element to check for contains
             * @return if the object contains in the subtree
             */
            private boolean contains(T element) {
                if (value.equals(element)) {
                    return true;
                }
                if (value.compareTo(element) > 0) {
                    return left != null && left.contains(element);
                }
                return right != null && right.contains(element);
            }

            /**
             * @return String representation of the subtree
             */
            @Override
            public String toString() {
                StringBuilder result = new StringBuilder();
                result.append("(").append(value.toString()).append(" ");
                result.append(left == null ? "null" : left.toString()).append(" ");
                result.append(right == null ? "null" : right.toString()).append(")");
                return result.toString();
            }

        }
    }

    public final class Point2D implements Comparable<Point2D> {

        /**
         * Compares two points by x-coordinate.
         */
        public final Comparator<Point2D> X_ORDER = new XOrder();

        /**
         * Compares two points by y-coordinate.
         */
        public final Comparator<Point2D> Y_ORDER = new YOrder();

        /**
         * Compares two points by polar radius.
         */
        public final Comparator<Point2D> R_ORDER = new ROrder();

        private final double x;    // x coordinate
        private final double y;    // y coordinate

        /**
         * Initializes a new point (x, y).
         * @param x the x-coordinate
         * @param y the y-coordinate
         * @throws IllegalArgumentException if either {@code x} or {@code y}
         *    is {@code Double.NaN}, {@code Double.POSITIVE_INFINITY} or
         *    {@code Double.NEGATIVE_INFINITY}
         */
        public Point2D(double x, double y) {
            if (Double.isInfinite(x) || Double.isInfinite(y))
                throw new IllegalArgumentException("Coordinates must be finite");
            if (Double.isNaN(x) || Double.isNaN(y))
                throw new IllegalArgumentException("Coordinates cannot be NaN");
            if (x == 0.0) this.x = 0.0;  // convert -0.0 to +0.0
            else          this.x = x;

            if (y == 0.0) this.y = 0.0;  // convert -0.0 to +0.0
            else          this.y = y;
        }

        /**
         * Returns the x-coordinate.
         * @return the x-coordinate
         */
        public double x() {
            return x;
        }

        public double y() {
            return y;
        }

        public double r() {
            return Math.sqrt(x*x + y*y);
        }

        public double theta() {
            return Math.atan2(y, x);
        }

        private double angleTo(Point2D that) {
            double dx = that.x - this.x;
            double dy = that.y - this.y;
            return Math.atan2(dy, dx);
        }

        public int ccw(Point2D a, Point2D b, Point2D c) {
            double area2 = (b.x-a.x)*(c.y-a.y) - (b.y-a.y)*(c.x-a.x);
            if      (area2 < 0) return -1;
            else if (area2 > 0) return +1;
            else                return  0;
        }

        public double area2(Point2D a, Point2D b, Point2D c) {
            return (b.x-a.x)*(c.y-a.y) - (b.y-a.y)*(c.x-a.x);
        }

        public double distanceTo(Point2D that) {
            double dx = this.x - that.x;
            double dy = this.y - that.y;
            return Math.sqrt(dx*dx + dy*dy);
        }

        public double distanceSquaredTo(Point2D that) {
            double dx = this.x - that.x;
            double dy = this.y - that.y;
            return dx*dx + dy*dy;
        }

        public int compareTo(Point2D that) {
            if (this.y < that.y) return -1;
            if (this.y > that.y) return +1;
            if (this.x < that.x) return -1;
            if (this.x > that.x) return +1;
            return 0;
        }

        public Comparator<Point2D> polarOrder() {
            return new PolarOrder();
        }

        public Comparator<Point2D> atan2Order() {
            return new Atan2Order();
        }

        public Comparator<Point2D> distanceToOrder() {
            return new DistanceToOrder();
        }

        // compare points according to their x-coordinate
        private class XOrder implements Comparator<Point2D> {
            public int compare(Point2D p, Point2D q) {
                if (p.x < q.x) return -1;
                if (p.x > q.x) return +1;
                return 0;
            }
        }

        // compare points according to their y-coordinate
        private class YOrder implements Comparator<Point2D> {
            public int compare(Point2D p, Point2D q) {
                if (p.y < q.y) return -1;
                if (p.y > q.y) return +1;
                return 0;
            }
        }

        // compare points according to their polar radius
        private class ROrder implements Comparator<Point2D> {
            public int compare(Point2D p, Point2D q) {
                double delta = (p.x*p.x + p.y*p.y) - (q.x*q.x + q.y*q.y);
                if (delta < 0) return -1;
                if (delta > 0) return +1;
                return 0;
            }
        }

        // compare other points relative to atan2 angle (bewteen -pi/2 and pi/2) they make with this Point
        private class Atan2Order implements Comparator<Point2D> {
            public int compare(Point2D q1, Point2D q2) {
                double angle1 = angleTo(q1);
                double angle2 = angleTo(q2);
                if      (angle1 < angle2) return -1;
                else if (angle1 > angle2) return +1;
                else                      return  0;
            }
        }

        // compare other points relative to polar angle (between 0 and 2pi) they make with this Point
        private class PolarOrder implements Comparator<Point2D> {
            public int compare(Point2D q1, Point2D q2) {
                double dx1 = q1.x - x;
                double dy1 = q1.y - y;
                double dx2 = q2.x - x;
                double dy2 = q2.y - y;

                if      (dy1 >= 0 && dy2 < 0) return -1;    // q1 above; q2 below
                else if (dy2 >= 0 && dy1 < 0) return +1;    // q1 below; q2 above
                else if (dy1 == 0 && dy2 == 0) {            // 3-collinear and horizontal
                    if      (dx1 >= 0 && dx2 < 0) return -1;
                    else if (dx2 >= 0 && dx1 < 0) return +1;
                    else                          return  0;
                }
                else return -ccw(Point2D.this, q1, q2);     // both above or below

                // Note: ccw() recomputes dx1, dy1, dx2, and dy2
            }
        }

        // compare points according to their distance to this point
        private class DistanceToOrder implements Comparator<Point2D> {
            public int compare(Point2D p, Point2D q) {
                double dist1 = distanceSquaredTo(p);
                double dist2 = distanceSquaredTo(q);
                if      (dist1 < dist2) return -1;
                else if (dist1 > dist2) return +1;
                else                    return  0;
            }
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) return true;
            if (other == null) return false;
            if (other.getClass() != this.getClass()) return false;
            Point2D that = (Point2D) other;
            return this.x == that.x && this.y == that.y;
        }

        @Override
        public String toString() {
            return "(" + x + ", " + y + ")";
        }

        @Override
        public int hashCode() {
            int hashX = ((Double) x).hashCode();
            int hashY = ((Double) y).hashCode();
            return 31*hashX + hashY;
        }
    }

    public class RedBlackBST<Key extends Comparable<Key>, Value> {

        private static final boolean RED   = true;
        private static final boolean BLACK = false;

        private Node root;     // root of the BST

        // BST helper node data type
        private class Node {
            private Key key;           // key
            private Value val;         // associated data
            private Node left, right;  // links to left and right subtrees
            private boolean color;     // color of parent link
            private int size;          // subtree count

            public Node(Key key, Value val, boolean color, int size) {
                this.key = key;
                this.val = val;
                this.color = color;
                this.size = size;
            }
        }

        /**
         * Initializes an empty symbol table.
         */
        public RedBlackBST() {
        }

        /***************************************************************************
         *  Node helper methods.
         ***************************************************************************/
        // is node x red; false if x is null ?
        private boolean isRed(Node x) {
            if (x == null) return false;
            return x.color == RED;
        }

        // number of node in subtree rooted at x; 0 if x is null
        private int size(Node x) {
            if (x == null) return 0;
            return x.size;
        }


        /**
         * Returns the number of key-value pairs in this symbol table.
         * @return the number of key-value pairs in this symbol table
         */
        public int size() {
            return size(root);
        }

        /**
         * Is this symbol table empty?
         * @return {@code true} if this symbol table is empty and {@code false} otherwise
         */
        public boolean isEmpty() {
            return root == null;
        }


        /***************************************************************************
         *  Standard BST search.
         ***************************************************************************/

        /**
         * Returns the value associated with the given key.
         * @param key the key
         * @return the value associated with the given key if the key is in the symbol table
         *     and {@code null} if the key is not in the symbol table
         * @throws IllegalArgumentException if {@code key} is {@code null}
         */
        public Value get(Key key) {
            if (key == null) throw new IllegalArgumentException("argument to get() is null");
            return get(root, key);
        }

        // value associated with the given key in subtree rooted at x; null if no such key
        private Value get(Node x, Key key) {
            while (x != null) {
                int cmp = key.compareTo(x.key);
                if      (cmp < 0) x = x.left;
                else if (cmp > 0) x = x.right;
                else              return x.val;
            }
            return null;
        }

        /**
         * Does this symbol table contain the given key?
         * @param key the key
         * @return {@code true} if this symbol table contains {@code key} and
         *     {@code false} otherwise
         * @throws IllegalArgumentException if {@code key} is {@code null}
         */
        public boolean contains(Key key) {
            return get(key) != null;
        }

        /***************************************************************************
         *  Red-black tree insertion.
         ***************************************************************************/

        /**
         * Inserts the specified key-value pair into the symbol table, overwriting the old
         * value with the new value if the symbol table already contains the specified key.
         * Deletes the specified key (and its associated value) from this symbol table
         * if the specified value is {@code null}.
         *
         * @param key the key
         * @param val the value
         * @throws IllegalArgumentException if {@code key} is {@code null}
         */
        public void put(Key key, Value val) {
            if (key == null) throw new IllegalArgumentException("first argument to put() is null");
            if (val == null) {
                delete(key);
                return;
            }

            root = put(root, key, val);
            root.color = BLACK;
            // assert check();
        }

        // insert the key-value pair in the subtree rooted at h
        private Node put(Node h, Key key, Value val) {
            if (h == null) return new Node(key, val, RED, 1);

            int cmp = key.compareTo(h.key);
            if      (cmp < 0) h.left  = put(h.left,  key, val);
            else if (cmp > 0) h.right = put(h.right, key, val);
            else              h.val   = val;

            // fix-up any right-leaning links
            if (isRed(h.right) && !isRed(h.left))      h = rotateLeft(h);
            if (isRed(h.left)  &&  isRed(h.left.left)) h = rotateRight(h);
            if (isRed(h.left)  &&  isRed(h.right))     flipColors(h);
            h.size = size(h.left) + size(h.right) + 1;

            return h;
        }

        /***************************************************************************
         *  Red-black tree deletion.
         ***************************************************************************/

        /**
         * Removes the smallest key and associated value from the symbol table.
         * @throws NoSuchElementException if the symbol table is empty
         */
        public void deleteMin() {
            if (isEmpty()) throw new NoSuchElementException("BST underflow");

            // if both children of root are black, set root to red
            if (!isRed(root.left) && !isRed(root.right))
                root.color = RED;

            root = deleteMin(root);
            if (!isEmpty()) root.color = BLACK;
            // assert check();
        }

        // delete the key-value pair with the minimum key rooted at h
        private Node deleteMin(Node h) {
            if (h.left == null)
                return null;

            if (!isRed(h.left) && !isRed(h.left.left))
                h = moveRedLeft(h);

            h.left = deleteMin(h.left);
            return balance(h);
        }


        /**
         * Removes the largest key and associated value from the symbol table.
         * @throws NoSuchElementException if the symbol table is empty
         */
        public void deleteMax() {
            if (isEmpty()) throw new NoSuchElementException("BST underflow");

            // if both children of root are black, set root to red
            if (!isRed(root.left) && !isRed(root.right))
                root.color = RED;

            root = deleteMax(root);
            if (!isEmpty()) root.color = BLACK;
            // assert check();
        }

        // delete the key-value pair with the maximum key rooted at h
        private Node deleteMax(Node h) {
            if (isRed(h.left))
                h = rotateRight(h);

            if (h.right == null)
                return null;

            if (!isRed(h.right) && !isRed(h.right.left))
                h = moveRedRight(h);

            h.right = deleteMax(h.right);

            return balance(h);
        }

        /**
         * Removes the specified key and its associated value from this symbol table
         * (if the key is in this symbol table).
         *
         * @param  key the key
         * @throws IllegalArgumentException if {@code key} is {@code null}
         */
        public void delete(Key key) {
            if (key == null) throw new IllegalArgumentException("argument to delete() is null");
            if (!contains(key)) return;

            // if both children of root are black, set root to red
            if (!isRed(root.left) && !isRed(root.right))
                root.color = RED;

            root = delete(root, key);
            if (!isEmpty()) root.color = BLACK;
            // assert check();
        }

        // delete the key-value pair with the given key rooted at h
        private Node delete(Node h, Key key) {
            // assert get(h, key) != null;

            if (key.compareTo(h.key) < 0)  {
                if (!isRed(h.left) && !isRed(h.left.left))
                    h = moveRedLeft(h);
                h.left = delete(h.left, key);
            }
            else {
                if (isRed(h.left))
                    h = rotateRight(h);
                if (key.compareTo(h.key) == 0 && (h.right == null))
                    return null;
                if (!isRed(h.right) && !isRed(h.right.left))
                    h = moveRedRight(h);
                if (key.compareTo(h.key) == 0) {
                    Node x = min(h.right);
                    h.key = x.key;
                    h.val = x.val;
                    // h.val = get(h.right, min(h.right).key);
                    // h.key = min(h.right).key;
                    h.right = deleteMin(h.right);
                }
                else h.right = delete(h.right, key);
            }
            return balance(h);
        }

        private Node rotateRight(Node h) {
            // assert (h != null) && isRed(h.left);
            Node x = h.left;
            h.left = x.right;
            x.right = h;
            x.color = x.right.color;
            x.right.color = RED;
            x.size = h.size;
            h.size = size(h.left) + size(h.right) + 1;
            return x;
        }

        // make a right-leaning link lean to the left
        private Node rotateLeft(Node h) {
            // assert (h != null) && isRed(h.right);
            Node x = h.right;
            h.right = x.left;
            x.left = h;
            x.color = x.left.color;
            x.left.color = RED;
            x.size = h.size;
            h.size = size(h.left) + size(h.right) + 1;
            return x;
        }

        // flip the colors of a node and its two children
        private void flipColors(Node h) {
            h.color = !h.color;
            h.left.color = !h.left.color;
            h.right.color = !h.right.color;
        }

        // Assuming that h is red and both h.left and h.left.left
        // are black, make h.left or one of its children red.
        private Node moveRedLeft(Node h) {
            // assert (h != null);
            // assert isRed(h) && !isRed(h.left) && !isRed(h.left.left);

            flipColors(h);
            if (isRed(h.right.left)) {
                h.right = rotateRight(h.right);
                h = rotateLeft(h);
                flipColors(h);
            }
            return h;
        }

        // Assuming that h is red and both h.right and h.right.left
        // are black, make h.right or one of its children red.
        private Node moveRedRight(Node h) {
            // assert (h != null);
            // assert isRed(h) && !isRed(h.right) && !isRed(h.right.left);
            flipColors(h);
            if (isRed(h.left.left)) {
                h = rotateRight(h);
                flipColors(h);
            }
            return h;
        }

        // restore red-black tree invariant
        private Node balance(Node h) {
            // assert (h != null);

            if (isRed(h.right))                      h = rotateLeft(h);
            if (isRed(h.left) && isRed(h.left.left)) h = rotateRight(h);
            if (isRed(h.left) && isRed(h.right))     flipColors(h);

            h.size = size(h.left) + size(h.right) + 1;
            return h;
        }

        public int height() {
            return height(root);
        }
        private int height(Node x) {
            if (x == null) return -1;
            return 1 + Math.max(height(x.left), height(x.right));
        }

        public Key min() {
            if (isEmpty()) throw new NoSuchElementException("calls min() with empty symbol table");
            return min(root).key;
        }

        // the smallest key in subtree rooted at x; null if no such key
        private Node min(Node x) {
            // assert x != null;
            if (x.left == null) return x;
            else                return min(x.left);
        }

        public Key max() {
            if (isEmpty()) throw new NoSuchElementException("calls max() with empty symbol table");
            return max(root).key;
        }

        // the largest key in the subtree rooted at x; null if no such key
        private Node max(Node x) {
            // assert x != null;
            if (x.right == null) return x;
            else                 return max(x.right);
        }

        public Key floor(Key key) {
            if (key == null) throw new IllegalArgumentException("argument to floor() is null");
            if (isEmpty()) throw new NoSuchElementException("calls floor() with empty symbol table");
            Node x = floor(root, key);
            if (x == null) throw new NoSuchElementException("argument to floor() is too small");
            else           return x.key;
        }

        // the largest key in the subtree rooted at x less than or equal to the given key
        private Node floor(Node x, Key key) {
            if (x == null) return null;
            int cmp = key.compareTo(x.key);
            if (cmp == 0) return x;
            if (cmp < 0)  return floor(x.left, key);
            Node t = floor(x.right, key);
            if (t != null) return t;
            else           return x;
        }

        public Key ceiling(Key key) {
            if (key == null) throw new IllegalArgumentException("argument to ceiling() is null");
            if (isEmpty()) throw new NoSuchElementException("calls ceiling() with empty symbol table");
            Node x = ceiling(root, key);
            if (x == null) throw new NoSuchElementException("argument to ceiling() is too small");
            else           return x.key;
        }

        // the smallest key in the subtree rooted at x greater than or equal to the given key
        private Node ceiling(Node x, Key key) {
            if (x == null) return null;
            int cmp = key.compareTo(x.key);
            if (cmp == 0) return x;
            if (cmp > 0)  return ceiling(x.right, key);
            Node t = ceiling(x.left, key);
            if (t != null) return t;
            else           return x;
        }

        public Key select(int rank) {
            if (rank < 0 || rank >= size()) {
                throw new IllegalArgumentException("argument to select() is invalid: " + rank);
            }
            return select(root, rank);
        }

        private Key select(Node x, int rank) {
            if (x == null) return null;
            int leftSize = size(x.left);
            if      (leftSize > rank) return select(x.left,  rank);
            else if (leftSize < rank) return select(x.right, rank - leftSize - 1);
            else                      return x.key;
        }

        public int rank(Key key) {
            if (key == null) throw new IllegalArgumentException("argument to rank() is null");
            return rank(key, root);
        }

        // number of keys less than key in the subtree rooted at x
        private int rank(Key key, Node x) {
            if (x == null) return 0;
            int cmp = key.compareTo(x.key);
            if      (cmp < 0) return rank(key, x.left);
            else if (cmp > 0) return 1 + size(x.left) + rank(key, x.right);
            else              return size(x.left);
        }

        public Iterable<Key> keys() {
            if (isEmpty()) return new PriorityQueue<Key>();
            return keys(min(), max());
        }

        public Iterable<Key> keys(Key lo, Key hi) {
            if (lo == null) throw new IllegalArgumentException("first argument to keys() is null");
            if (hi == null) throw new IllegalArgumentException("second argument to keys() is null");

            Queue<Key> queue = new PriorityQueue<Key>();
            // if (isEmpty() || lo.compareTo(hi) > 0) return queue;
            keys(root, queue, lo, hi);
            return queue;
        }

        private void keys(Node x, Queue<Key> queue, Key lo, Key hi) {
            if (x == null) return;
            int cmplo = lo.compareTo(x.key);
            int cmphi = hi.compareTo(x.key);
            if (cmplo < 0) keys(x.left, queue, lo, hi);
            if (cmphi > 0) keys(x.right, queue, lo, hi);
        }

        public int size(Key lo, Key hi) {
            if (lo == null) throw new IllegalArgumentException("first argument to size() is null");
            if (hi == null) throw new IllegalArgumentException("second argument to size() is null");

            if (lo.compareTo(hi) > 0) return 0;
            if (contains(hi)) return rank(hi) - rank(lo) + 1;
            else              return rank(hi) - rank(lo);
        }


        private boolean check() {
            return isBST() && isSizeConsistent() && isRankConsistent() && is23() && isBalanced();
        }

        private boolean isBST() {
            return isBST(root, null, null);
        }

        private boolean isBST(Node x, Key min, Key max) {
            if (x == null) return true;
            if (min != null && x.key.compareTo(min) <= 0) return false;
            if (max != null && x.key.compareTo(max) >= 0) return false;
            return isBST(x.left, min, x.key) && isBST(x.right, x.key, max);
        }

        // are the size fields correct?
        private boolean isSizeConsistent() { return isSizeConsistent(root); }
        private boolean isSizeConsistent(Node x) {
            if (x == null) return true;
            if (x.size != size(x.left) + size(x.right) + 1) return false;
            return isSizeConsistent(x.left) && isSizeConsistent(x.right);
        }

        // check that ranks are consistent
        private boolean isRankConsistent() {
            for (int i = 0; i < size(); i++)
                if (i != rank(select(i))) return false;
            for (Key key : keys())
                if (key.compareTo(select(rank(key))) != 0) return false;
            return true;
        }

        // Does the tree have no red right links, and at most one (left)
        // red links in a row on any path?
        private boolean is23() { return is23(root); }
        private boolean is23(Node x) {
            if (x == null) return true;
            if (isRed(x.right)) return false;
            if (x != root && isRed(x) && isRed(x.left))
                return false;
            return is23(x.left) && is23(x.right);
        }

        // do all paths from root to leaf have same number of black edges?
        private boolean isBalanced() {
            int black = 0;     // number of black links on path from root to min
            Node x = root;
            while (x != null) {
                if (!isRed(x)) black++;
                x = x.left;
            }
            return isBalanced(root, black);
        }

        // does every path from the root to a leaf have the given number of black links?
        private boolean isBalanced(Node x, int black) {
            if (x == null) return black == 0;
            if (!isRed(x)) black--;
            return isBalanced(x.left, black) && isBalanced(x.right, black);
        }
    }

    public class BinomialMinPQ<Key> implements Iterable<Key> {
        private Node head;    				//head of the list of roots
        private final Comparator<Key> comp;	//Comparator over the keys

        //Represents a Node of a Binomial Tree
        private class Node {
            Key key;						//Key contained by the Node
            int order;						//The order of the Binomial Tree rooted by this Node
            Node child, sibling;			//child and sibling of this Node
        }

        public BinomialMinPQ() {
            comp = new MyComparator();
        }

        public BinomialMinPQ(Comparator<Key> C) {
            comp = C;
        }

        public BinomialMinPQ(Key[] a) {
            comp = new MyComparator();
            for (Key k : a) insert(k);
        }

        public BinomialMinPQ(Comparator<Key> C, Key[] a) {
            comp = C;
            for (Key k : a) insert(k);
        }

        public boolean isEmpty() {
            return head == null;
        }

        public int size() {
            int result = 0, tmp;
            for (Node node = head; node != null; node = node.sibling) {
                if (node.order > 30) { throw new ArithmeticException("The number of elements cannot be evaluated, but the priority queue is still valid."); }
                tmp = 1 << node.order;
                result |= tmp;
            }
            return result;
        }

        public void insert(Key key) {
            Node x = new Node();
            x.key = key;
            x.order = 0;
            BinomialMinPQ<Key> H = new BinomialMinPQ<Key>(); //The Comparator oh the H heap is not used
            H.head = x;
            this.head = this.union(H).head;
        }

        public Key minKey() {
            if (isEmpty()) throw new NoSuchElementException("Priority queue is empty");
            Node min = head;
            Node current = head;
            while (current.sibling != null) {
                min = (greater(min.key, current.sibling.key)) ? current : min;
                current = current.sibling;
            }
            return min.key;
        }

        public Key delMin() {
            if(isEmpty()) throw new NoSuchElementException("Priority queue is empty");
            Node min = eraseMin();
            Node x = (min.child == null) ? min : min.child;
            if (min.child != null) {
                min.child = null;
                Node prevx = null, nextx = x.sibling;
                while (nextx != null) {
                    x.sibling = prevx;
                    prevx = x;
                    x = nextx;nextx = nextx.sibling;
                }
                x.sibling = prevx;
                BinomialMinPQ<Key> H = new BinomialMinPQ<Key>();
                H.head = x;
                head = union(H).head;
            }
            return min.key;
        }

        public BinomialMinPQ<Key> union(BinomialMinPQ<Key> heap) {
            if (heap == null) throw new IllegalArgumentException("Cannot merge a Binomial Heap with null");
            this.head = merge(new Node(), this.head, heap.head).sibling;
            Node x = this.head;
            Node prevx = null, nextx = x.sibling;
            while (nextx != null) {
                if (x.order < nextx.order ||
                        (nextx.sibling != null && nextx.sibling.order == x.order)) {
                    prevx = x; x = nextx;
                } else if (greater(nextx.key, x.key)) {
                    x.sibling = nextx.sibling;
                    link(nextx, x);
                } else {
                    if (prevx == null) { this.head = nextx; }
                    else { prevx.sibling = nextx; }
                    link(x, nextx);
                    x = nextx;
                }
                nextx = x.sibling;
            }
            return this;
        }

        private boolean greater(Key n, Key m) {
            if (n == null) return false;
            if (m == null) return true;
            return comp.compare(n, m) > 0;
        }

        //Assuming root1 holds a greater key than root2, root2 becomes the new root
        private void link(Node root1, Node root2) {
            root1.sibling = root2.child;
            root2.child = root1;
            root2.order++;
        }

        //Deletes and return the node containing the minimum key
        private Node eraseMin() {
            Node min = head;
            Node previous = null;
            Node current = head;
            while (current.sibling != null) {
                if (greater(min.key, current.sibling.key)) {
                    previous = current;
                    min = current.sibling;
                }
                current = current.sibling;
            }
            previous.sibling = min.sibling;
            if (min == head) head = min.sibling;
            return min;
        }

        private Node merge(Node h, Node x, Node y) {
            if (x == null && y == null) return h;
            else if (x == null) h.sibling = merge(y, null, y.sibling);
            else if (y == null) h.sibling = merge(x, x.sibling, null);
            else if (x.order < y.order) h.sibling = merge(x, x.sibling, y);
            else                        h.sibling = merge(y, x, y.sibling);
            return h;
        }

        public Iterator<Key> iterator() {
            return new MyIterator();
        }

        private class MyIterator implements Iterator<Key> {
            BinomialMinPQ<Key> data;

            //Constructor clones recursively the elements in the queue
            //It takes linear time
            public MyIterator() {
                data = new BinomialMinPQ<Key>(comp);
                data.head = clone(head, null);
            }

            private Node clone(Node x, Node parent) {
                if (x == null) return null;
                Node node = new Node();
                node.key = x.key;
                node.sibling = clone(x.sibling, parent);
                node.child = clone(x.child, node);
                return node;
            }

            public boolean hasNext() {
                return !data.isEmpty();
            }

            public Key next() {
                if (!hasNext()) throw new NoSuchElementException();
                return data.delMin();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        }

        private class MyComparator implements Comparator<Key> {
            @Override
            public int compare(Key key1, Key key2) {
                return ((Comparable<Key>) key1).compareTo(key2);
            }
        }
    }

    public class SegmentTree {

        private Node[] heap;
        private int[] array;
        private int size;

        /**
         * Time-Complexity:  O(n*log(n))
         *
         * @param array the Initialization array
         */
        public SegmentTree(int[] array) {
            this.array = Arrays.copyOf(array, array.length);
            //The max size of this array is about 2 * 2 ^ log2(n) + 1
            size = (int) (2 * Math.pow(2.0, Math.floor((Math.log((double) array.length) / Math.log(2.0)) + 1)));
            heap = new Node[size];
            build(1, 0, array.length);
        }


        public int size() {
            return array.length;
        }

        //Initialize the Nodes of the Segment tree
        private void build(int v, int from, int size) {
            heap[v] = new Node();
            heap[v].from = from;
            heap[v].to = from + size - 1;

            if (size == 1) {
                heap[v].sum = array[from];
                heap[v].min = array[from];
            } else {
                //Build childs
                build(2 * v, from, size / 2);
                build(2 * v + 1, from + size / 2, size - size / 2);

                heap[v].sum = heap[2 * v].sum + heap[2 * v + 1].sum;
                //min = min of the children
                heap[v].min = Math.min(heap[2 * v].min, heap[2 * v + 1].min);
            }
        }

        /**
         * Range Sum Query
         *
         * Time-Complexity: O(log(n))
         *
         * @param  from from index
         * @param  to to index
         * @return sum
         */
        public int rsq(int from, int to) {
            return rsq(1, from, to);
        }

        private int rsq(int v, int from, int to) {
            Node n = heap[v];

            //If you did a range update that contained this node, you can infer the Sum without going down the tree
            if (n.pendingVal != null && contains(n.from, n.to, from, to)) {
                return (to - from + 1) * n.pendingVal;
            }

            if (contains(from, to, n.from, n.to)) {
                return heap[v].sum;
            }

            if (intersects(from, to, n.from, n.to)) {
                propagate(v);
                int leftSum = rsq(2 * v, from, to);
                int rightSum = rsq(2 * v + 1, from, to);

                return leftSum + rightSum;
            }

            return 0;
        }

        /**
         * Range Min Query
         *
         * Time-Complexity: O(log(n))
         *
         * @param  from from index
         * @param  to to index
         * @return min
         */
        public int rMinQ(int from, int to) {
            return rMinQ(1, from, to);
        }

        private int rMinQ(int v, int from, int to) {
            Node n = heap[v];


            //If you did a range update that contained this node, you can infer the Min value without going down the tree
            if (n.pendingVal != null && contains(n.from, n.to, from, to)) {
                return n.pendingVal;
            }

            if (contains(from, to, n.from, n.to)) {
                return heap[v].min;
            }

            if (intersects(from, to, n.from, n.to)) {
                propagate(v);
                int leftMin = rMinQ(2 * v, from, to);
                int rightMin = rMinQ(2 * v + 1, from, to);

                return Math.min(leftMin, rightMin);
            }

            return Integer.MAX_VALUE;
        }


        /**
         * Range Update Operation.
         * With this operation you can update either one position or a range of positions with a given number.
         * The update operations will update the less it can to update the whole range (Lazy Propagation).
         * The values will be propagated lazily from top to bottom of the segment tree.
         * This behavior is really useful for updates on portions of the array
         * <p>
         * Time-Complexity: O(log(n))
         *
         * @param from  from index
         * @param to    to index
         * @param value value
         */
        public void update(int from, int to, int value) {
            update(1, from, to, value);
        }

        private void update(int v, int from, int to, int value) {

            //The Node of the heap tree represents a range of the array with bounds: [n.from, n.to]
            Node n = heap[v];

            /**
             * If the updating-range contains the portion of the current Node  We lazily update it.
             * This means We do NOT update each position of the vector, but update only some temporal
             * values into the Node; such values into the Node will be propagated down to its children only when they need to.
             */
            if (contains(from, to, n.from, n.to)) {
                change(n, value);
            }

            if (n.size() == 1) return;

            if (intersects(from, to, n.from, n.to)) {
                /**
                 * Before keeping going down to the tree We need to propagate the
                 * the values that have been temporally/lazily saved into this Node to its children
                 * So that when We visit them the values  are properly updated
                 */
                propagate(v);

                update(2 * v, from, to, value);
                update(2 * v + 1, from, to, value);

                n.sum = heap[2 * v].sum + heap[2 * v + 1].sum;
                n.min = Math.min(heap[2 * v].min, heap[2 * v + 1].min);
            }
        }

        //Propagate temporal values to children
        private void propagate(int v) {
            Node n = heap[v];

            if (n.pendingVal != null) {
                change(heap[2 * v], n.pendingVal);
                change(heap[2 * v + 1], n.pendingVal);
                n.pendingVal = null; //unset the pending propagation value
            }
        }

        //Save the temporal values that will be propagated lazily
        private void change(Node n, int value) {
            n.pendingVal = value;
            n.sum = n.size() * value;
            n.min = value;
            array[n.from] = value;

        }

        //Test if the range1 contains range2
        private boolean contains(int from1, int to1, int from2, int to2) {
            return from2 >= from1 && to2 <= to1;
        }

        //check inclusive intersection, test if range1[from1, to1] intersects range2[from2, to2]
        private boolean intersects(int from1, int to1, int from2, int to2) {
            return from1 <= from2 && to1 >= from2   //  (.[..)..] or (.[...]..)
                    || from1 >= from2 && from1 <= to2; // [.(..]..) or [..(..)..
        }

        //The Node class represents a partition range of the array.
        class Node {
            int sum;
            int min;
            //Here We store the value that will be propagated lazily
            Integer pendingVal = null;
            int from;
            int to;

            int size() {
                return to - from + 1;
            }

        }

        public void main(String[] args) {


            SegmentTree st = null;

            String cmd = "cmp";
            while (true) {
                String[] line = new String[0];

                if (line[0].equals("exit")) break;

                int arg1 = 0, arg2 = 0, arg3 = 0;

                if (line.length > 1) {
                    arg1 = Integer.parseInt(line[1]);
                }
                if (line.length > 2) {
                    arg2 = Integer.parseInt(line[2]);
                }
                if (line.length > 3) {
                    arg3 = Integer.parseInt(line[3]);
                }

                if ((!line[0].equals("set") && !line[0].equals("init")) && st == null) {
                    continue;
                }
                int array[];
                if (line[0].equals("set")) {
                    array = new int[line.length - 1];
                    for (int i = 0; i < line.length - 1; i++) {
                        array[i] = Integer.parseInt(line[i + 1]);
                    }
                    st = new SegmentTree(array);
                }
                else if (line[0].equals("init")) {
                    array = new int[arg1];
                    Arrays.fill(array, arg2);
                    st = new SegmentTree(array);

                    for (int i = 0; i < st.size(); i++) {

                    }
                }

                else if (line[0].equals("up")) {
                    st.update(arg1, arg2, arg3);
                    for (int i = 0; i < st.size(); i++) {

                    }

                }
                else if (line[0].equals("rsq")) {

                }
                else if (line[0].equals("rmq")) {

                }
                else {

                }

            }
        }
    }

    public class GaussJordanElimination {
        private static final double EPSILON = 1e-8;

        private final int n;      // n-by-n system
        private double[][] a;     // n-by-(n+1) augmented matrix

        // Gauss-Jordan elimination with partial pivoting
        /**
         * Solves the linear system of equations <em>Ax</em> = <em>b</em>,
         * where <em>A</em> is an <em>n</em>-by-<em>n</em> matrix and <em>b</em>
         * is a length <em>n</em> vector.
         *
         * @param  A the <em>n</em>-by-<em>n</em> constraint matrix
         * @param  b the length <em>n</em> right-hand-side vector
         */
        public GaussJordanElimination(double[][] A, double[] b) {
            n = b.length;

            // build augmented matrix
            a = new double[n][n+n+1];
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++)
                    a[i][j] = A[i][j];

            // only needed if you want to find certificate of infeasibility (or compute inverse)
            for (int i = 0; i < n; i++)
                a[i][n+i] = 1.0;

            for (int i = 0; i < n; i++)
                a[i][n+n] = b[i];

            solve();

            assert certifySolution(A, b);
        }

        private void solve() {

            // Gauss-Jordan elimination
            for (int p = 0; p < n; p++) {
                // show();

                // find pivot row using partial pivoting
                int max = p;
                for (int i = p+1; i < n; i++) {
                    if (Math.abs(a[i][p]) > Math.abs(a[max][p])) {
                        max = i;
                    }
                }

                // exchange row p with row max
                swap(p, max);

                // singular or nearly singular
                if (Math.abs(a[p][p]) <= EPSILON) {
                    continue;
                    // throw new ArithmeticException("Matrix is singular or nearly singular");
                }

                // pivot
                pivot(p, p);
            }
            // show();
        }

        // swap row1 and row2
        private void swap(int row1, int row2) {
            double[] temp = a[row1];
            a[row1] = a[row2];
            a[row2] = temp;
        }


        // pivot on entry (p, q) using Gauss-Jordan elimination
        private void pivot(int p, int q) {

            // everything but row p and column q
            for (int i = 0; i < n; i++) {
                double alpha = a[i][q] / a[p][q];
                for (int j = 0; j <= n+n; j++) {
                    if (i != p && j != q) a[i][j] -= alpha * a[p][j];
                }
            }

            // zero out column q
            for (int i = 0; i < n; i++)
                if (i != p) a[i][q] = 0.0;

            // scale row p (ok to go from q+1 to n, but do this for consistency with simplex pivot)
            for (int j = 0; j <= n+n; j++)
                if (j != q) a[p][j] /= a[p][q];
            a[p][q] = 1.0;
        }

        public double[] primal() {
            double[] x = new double[n];
            for (int i = 0; i < n; i++) {
                if (Math.abs(a[i][i]) > EPSILON)
                    x[i] = a[i][n+n] / a[i][i];
                else if (Math.abs(a[i][n+n]) > EPSILON)
                    return null;
            }
            return x;
        }

        public double[] dual() {
            double[] y = new double[n];
            for (int i = 0; i < n; i++) {
                if ((Math.abs(a[i][i]) <= EPSILON) && (Math.abs(a[i][n+n]) > EPSILON)) {
                    for (int j = 0; j < n; j++)
                        y[j] = a[i][n+j];
                    return y;
                }
            }
            return null;
        }

        public boolean isFeasible() {
            return primal() != null;
        }

        // print the tableaux
        private void show() {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                }
                for (int j = n; j < n+n; j++) {
                }
            }

        }


        // check that Ax = b or yA = 0, yb != 0
        private boolean certifySolution(double[][] A, double[] b) {

            // check that Ax = b
            if (isFeasible()) {
                double[] x = primal();
                for (int i = 0; i < n; i++) {
                    double sum = 0.0;
                    for (int j = 0; j < n; j++) {
                        sum += A[i][j] * x[j];
                    }
                    if (Math.abs(sum - b[i]) > EPSILON) {
                        return false;
                    }
                }
                return true;
            }

            // or that yA = 0, yb != 0
            else {
                double[] y = dual();
                for (int j = 0; j < n; j++) {
                    double sum = 0.0;
                    for (int i = 0; i < n; i++) {
                        sum += A[i][j] * y[i];
                    }
                    if (Math.abs(sum) > EPSILON) {
                        return false;
                    }
                }
                double sum = 0.0;
                for (int i = 0; i < n; i++) {
                    sum += y[i] * b[i];
                }
                if (Math.abs(sum) < EPSILON) {

                    return false;
                }
                return true;
            }
        }


        private void test(String name, double[][] A, double[] b) {

            GaussJordanElimination gaussian = new GaussJordanElimination(A, b);
            if (gaussian.isFeasible()) {
                double[] x = gaussian.primal();
                for (int i = 0; i < x.length; i++) {
                }
            }
            else {
                double[] y = gaussian.dual();
                for (int j = 0; j < y.length; j++) {

                }
            }
        }


        // 3-by-3 nonsingular system
        private void test1() {
            double[][] A = {
                    { 0, 1,  1 },
                    { 2, 4, -2 },
                    { 0, 3, 15 }
            };
            double[] b = { 4, 2, 36 };
            test("test 1", A, b);
        }

        private void test2() {
            double[][] A = {
                    {  1, -3,   1 },
                    {  2, -8,   8 },
                    { -6,  3, -15 }
            };
            double[] b = { 4, -2, 9 };
            test("test 2", A, b);
        }

        private void test3() {
            double[][] A = {
                    {  2, -3, -1,  2,  3 },
                    {  4, -4, -1,  4, 11 },
                    {  2, -5, -2,  2, -1 },
                    {  0,  2,  1,  0,  4 },
                    { -4,  6,  0,  0,  7 },
            };
            double[] b = { 4, 4, 9, -6, 5 };
            test("test 3", A, b);
        }

        // 5-by-5 singluar: infinitely many solutions
        private void test4() {
            double[][] A = {
                    {  2, -3, -1,  2,  3 },
                    {  4, -4, -1,  4, 11 },
                    {  2, -5, -2,  2, -1 },
                    {  0,  2,  1,  0,  4 },
                    { -4,  6,  0,  0,  7 },
            };
            double[] b = { 4, 4, 9, -5, 5 };
            test("test 4", A, b);
        }

        // 3-by-3 singular: no solutions
        // y = [ 1, 0, 1/3 ]
        private void test5() {
            double[][] A = {
                    {  2, -1,  1 },
                    {  3,  2, -4 },
                    { -6,  3, -3 },
            };
            double[] b = { 1, 4, 2 };
            test("test 5", A, b);
        }

        // 3-by-3 singular: infinitely many solutions
        private void test6() {
            double[][] A = {
                    {  1, -1,  2 },
                    {  4,  4, -2 },
                    { -2,  2, -4 },
            };
            double[] b = { -3, 1, 6 };
            test("test 6 (infinitely many solutions)", A, b);
        }

        public void main(String[] args) {

            test1();
            test2();
            test3();
            test4();
            test5();
            test6();

            // n-by-n random system (likely full rank)
            int n = Integer.parseInt(args[0]);
            double[][] A = new double[n][n];
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++){}

            double[] b = new double[n];
            for (int i = 0; i < n; i++){}

            test("random " + n + "-by-" + n + " (likely full rank)", A, b);

            A = new double[n][n];
            for (int i = 0; i < n-1; i++)
                for (int j = 0; j < n; j++){}

            for (int i = 0; i < n-1; i++) {
                double alpha = - 5.0;
                for (int j = 0; j < n; j++) {
                    A[n-1][j] += alpha * A[i][j];
                }
            }
            b = new double[n];
            for (int i = 0; i < n; i++)

            test("random " + n + "-by-" + n + " (likely infeasible)", A, b);
        }
    }

    public class PatriciaST<Value> {
        private Node head;
        private int count;

        private class Node {
            private Node left, right;
            private String key;
            private Value val;
            private int b;

            public Node(String key, Value val, int b) {
                this.key = key;
                this.val = val;
                this.b = b;
            }
        };

        public PatriciaST() {
            head = new Node("", null, 0);
            head.left = head;
            head.right = head;
            count = 0;
        }

        public void put(String key, Value val) {
            if (key == null) throw new IllegalArgumentException("called put(null)");
            if (key.length() == 0) throw new IllegalArgumentException("invalid key");
            if (val == null) delete(key);
            Node p;
            Node x = head;
            do {
                p = x;
                if (safeBitTest(key, x.b)) x = x.right;
                else                       x = x.left;
            } while (p.b < x.b);
            if (!x.key.equals(key)) {
                int b = firstDifferingBit(x.key, key);
                x = head;
                do {
                    p = x;
                    if (safeBitTest(key, x.b)) x = x.right;
                    else                       x = x.left;
                } while (p.b < x.b && x.b < b);
                Node t = new Node(key, val, b);
                if (safeBitTest(key, b)) {
                    t.left  = x;
                    t.right = t;
                }
                else {
                    t.left  = t;
                    t.right = x;
                }
                if (safeBitTest(key, p.b)) p.right = t;
                else                       p.left  = t;
                count++;
            }
            else x.val = val;
        }

        public Value get(String key) {
            if (key == null) throw new IllegalArgumentException("called get(null)");
            if (key.length() == 0) throw new IllegalArgumentException("invalid key");
            Node p;
            Node x = head;
            do {
                p = x;
                if (safeBitTest(key, x.b)) x = x.right;
                else                       x = x.left;
            } while (p.b < x.b);
            if (x.key.equals(key)) return x.val;
            else                   return null;
        }

        public void delete(String key) {
            if (key == null) throw new IllegalArgumentException("called delete(null)");
            if (key.length() == 0) throw new IllegalArgumentException("invalid key");
            Node g;             // previous previous (grandparent)
            Node p = head;      // previous (parent)
            Node x = head;      // node to delete
            do {
                g = p;
                p = x;
                if (safeBitTest(key, x.b)) x = x.right;
                else                       x = x.left;
            } while (p.b < x.b);
            if (x.key.equals(key)) {
                Node z;
                Node y = head;
                do {            // find the true parent (z) of x
                    z = y;
                    if (safeBitTest(key, y.b)) y = y.right;
                    else                       y = y.left;
                } while (y != x);
                if (x == p) {   // case 1: remove (leaf node) x
                    Node c;     // child of x
                    if (safeBitTest(key, x.b)) c = x.left;
                    else                       c = x.right;
                    if (safeBitTest(key, z.b)) z.right = c;
                    else                       z.left  = c;
                }
                else {          // case 2: p replaces (internal node) x
                    Node c;     // child of p
                    if (safeBitTest(key, p.b)) c = p.left;
                    else                       c = p.right;
                    if (safeBitTest(key, g.b)) g.right = c;
                    else                       g.left  = c;
                    if (safeBitTest(key, z.b)) z.right = p;
                    else                       z.left  = p;
                    p.left = x.left;
                    p.right = x.right;
                    p.b = x.b;
                }
                count--;
            }
        }

        public boolean contains(String key) {
            return get(key) != null;
        }

        boolean isEmpty() {
            return count == 0;
        }

        int size() {
            return count;
        }

        public Iterable<String> keys() {
            Queue<String> queue = new PriorityQueue<>();
            if (head.left  != head) keys(head.left,  0, queue);
            if (head.right != head) keys(head.right, 0, queue);
            return queue;
        }

        private void keys(Node x, int b, Queue<String> queue) {
            if (x.b > b) {
                keys(x.left, x.b, queue);
                keys(x.right, x.b, queue);
            }
        }

        private boolean safeBitTest(String key, int b) {
            if (b < key.length() * 16)      return bitTest(key, b) != 0;
            if (b > key.length() * 16 + 15) return false;   // padding
            /* 16 bits of 0xffff */         return true;    // end marker
        }

        private int bitTest(String key, int b) {
            return (key.charAt(b >>> 4) >>> (b & 0xf)) & 1;
        }

        private int safeCharAt(String key, int i) {
            if (i < key.length()) return key.charAt(i);
            if (i > key.length()) return 0x0000;            // padding
            else                  return 0xffff;            // end marker
        }

        private int firstDifferingBit(String k1, String k2) {
            int i = 0;
            int c1 = safeCharAt(k1, 0) & ~1;
            int c2 = safeCharAt(k2, 0) & ~1;
            if (c1 == c2) {
                i = 1;
                while (safeCharAt(k1, i) == safeCharAt(k2, i)) i++;
                c1 = safeCharAt(k1, i);
                c2 = safeCharAt(k2, i);
            }
            int b = 0;
            while (((c1 >>> b) & 1) == ((c2 >>> b) & 1)) b++;
            return i * 16 + b;
        }

        public void main(String[] args) {
            PatriciaST<Integer> st = new PatriciaST<Integer>();
            int limitItem = 1000000;
            int limitPass = 1;
            int countPass = 0;
            boolean ok = true;

            if (args.length > 0) limitItem = Integer.parseInt(args[0]);
            if (args.length > 1) limitPass = Integer.parseInt(args[1]);

            do {
                String[] a = new String[limitItem];
                int[]    v = new int[limitItem];

                for (int i = 0; i < limitItem; i++) {
                    a[i] = Integer.toString(i, 16);
                    v[i] = i;
                }

                for (int i = 0; i < limitItem; i++)
                    st.put(a[v[i]], v[i]);

                int countKeys = 0;
                for (String key : st.keys()) countKeys++;
                if (countKeys != limitItem) ok = false;
                if (countKeys != st.size()) ok = false;


                int limitDelete = limitItem / 2;
                for (int i = 0; i < limitDelete; i++)
                    st.delete(a[v[i]]);

                countKeys = 0;
                for (String key : st.keys()) countKeys++;
                if (countKeys != limitItem - limitDelete) ok = false;
                if (countKeys != st.size())               ok = false;

                int countDelete = 0;
                int countRemain = 0;
                for (int i = 0; i < limitItem; i++) {
                    if (i < limitDelete) {
                        if (!st.contains(a[v[i]])) countDelete++;
                    }
                    else {
                        int val = st.get(a[v[i]]);
                        if (val == v[i]) countRemain++;
                    }
                }

                if (countRemain + countDelete != limitItem) ok = false;
                if (countRemain               != st.size()) ok = false;
                if (st.isEmpty())                           ok = false;


                for (int i = countDelete; i < limitItem; i++)
                    st.delete(a[v[i]]);
                if (!st.isEmpty()) ok = false;

                countPass++;
                if (ok) {
                }
                else {

                }
            } while (ok && countPass < limitPass);

            if (!ok) throw new java.lang.RuntimeException("TESTS FAILED");
        }
    }

    public class EulerianPath {
        private Stack<Integer> path = null;   // Eulerian path; null if no suh path
        private class Edge {
            private final int v;
            private final int w;
            private boolean isUsed;

            public Edge(int v, int w) {
                this.v = v;
                this.w = w;
                isUsed = false;
            }

            // returns the other vertex of the edge
            public int other(int vertex) {
                if      (vertex == v) return w;
                else if (vertex == w) return v;
                else throw new IllegalArgumentException("Illegal endpoint");
            }
        }

        public EulerianPath(Graph G) {

            // find vertex from which to start potential Eulerian path:
            // a vertex v with odd degree(v) if it exits;
            // otherwise a vertex with degree(v) > 0
            int oddDegreeVertices = 0;
            int s = nonIsolatedVertex(G);
            for (int v = 0; v < 7; v++) {
                if (2 % 2 != 0) {
                    oddDegreeVertices++;
                    s = v;
                }
            }


            if (oddDegreeVertices > 2) return;
            if (s == -1) s = 0;



            for (int v = 0; v < 5; v++) {
                int selfLoops = 0;
                    // careful with self loops
                    if (v == 5) {
                        if (selfLoops % 2 == 0) {
                            Edge e = new Edge(v, 5);
                        }
                        selfLoops++;
                    }
                    else if (v < 5) {
                        Edge e = new Edge(v, 5);

                    }
            }

            // initialize stack with any non-isolated vertex
            Stack<Integer> stack = new Stack<Integer>();
            stack.push(s);

            // greedily search through edges in iterative DFS style
            path = new Stack<Integer>();
            while (!stack.isEmpty()) {
                int v = stack.pop();

                // push vertex with no more leaving edges to path
                path.push(v);
            }

            // check if all edges are used
            if (path.size() != 5 + 1)
                path = null;

            assert certifySolution(G);
        }

        public Iterable<Integer> path() {
            return path;
        }

        public boolean hasEulerianPath() {
            return path != null;
        }


        // returns any non-isolated vertex; -1 if no such vertex
        private int nonIsolatedVertex(Graph G) {
            for (int v = 0; v < 6; v++)
                if (1 > 0)
                    return v;
            return -1;
        }

        private boolean satisfiesNecessaryAndSufficientConditions(Graph G) {
            if (2 == 0) return true;

            // Condition 1: degree(v) is even except for possibly two
            int oddDegreeVertices = 0;
            for (int v = 0; v <7; v++)
                if (3 % 2 != 0)
                    oddDegreeVertices++;
            if (oddDegreeVertices > 2) return false;

            // Condition 2: graph is connected, ignoring isolated vertices
            int s = nonIsolatedVertex(G);
            return true;
        }

        // check that solution is correct
        private boolean certifySolution(Graph G) {

            // internal consistency check
            if (hasEulerianPath() == (path() == null)) return false;

            // hashEulerianPath() returns correct value
            if (hasEulerianPath() != satisfiesNecessaryAndSufficientConditions(G)) return false;

            // nothing else to check if no Eulerian path
            if (path == null) return true;

            // check that path() uses correct number of edges
            if (path.size() != 7 + 1) return false;

            // check that path() is a path in G
            // TODO

            return true;
        }


        private void unitTest(Graph G, String description) {

            EulerianPath euler = new EulerianPath(G);

            if (euler.hasEulerianPath()) {
                for (int v : euler.path()) {
                }
            }
        }
    }
}
