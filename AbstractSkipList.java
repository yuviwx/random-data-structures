import java.util.NoSuchElementException;
import java.util.ArrayList;
import java.util.List;

abstract public class AbstractSkipList {
    final protected Node head;
    final protected Node tail;
    protected int size; // New field representing the size of the skip list in level 0

    public AbstractSkipList() {
        head = new Node(Integer.MIN_VALUE);
        tail = new Node(Integer.MAX_VALUE);
        increaseHeight();

        // Initialize the skip of head
        head.skip_size.add(0,1);

        // Initialize size field
        size = 0;
    }

    public void increaseHeight() {
        head.addLevel(tail, null);
        tail.addLevel(null, head);
    }

    abstract Node find(int key);

    abstract public int generateHeight();

    public Node search(int key) {
        Node curr = find(key);

        return curr.key() == key ? curr : null;
    }

    public Node insert(int key) {
        this.size++; // Increase the size of the skip list
        int nodeHeight = generateHeight();

        while (nodeHeight > head.height()) {
            increaseHeight();
        }

        Node prevNode = find(key);
        if (prevNode.key() == key) {
            return null;
        }

        Node newNode = new Node(key);

        for (int level = 0; level <= nodeHeight && prevNode != null; ++level) {
            Node nextNode = prevNode.getNext(level);

            newNode.addLevel(nextNode, prevNode);
            prevNode.setNext(level, newNode);
            nextNode.setPrev(level, newNode);

            // Adjust the skips between the nodes
            setSkips(prevNode, newNode, nextNode, level);

            while (prevNode != null && prevNode.height() == level) {
                prevNode = prevNode.getPrev(level);
            }
        }
        // Increase by 1 each node that is higher than newNode, and we want through it in the find method
        updateHeight(newNode.getPrev(nodeHeight), nodeHeight, true);
        return newNode;
    }

    /**
     * The function set the right width of skip each node have in level - 'level'
     * @param prev: The predecessor node of 'curr' in the desired level
     * @param curr: The current node that being added
     * @param next: The successor node of 'curr' in the desired level
     * @param level: The level of which the skips will be set
     * **/
    public void setSkips(Node prev, Node curr, Node next, int level){
        int prev_skip, my_skip;
        // Check if a new level was created and Initialize the sentinal('head') skip
        if(prev.skip_size.size() - 1 < level){
            my_skip = calculate_skip(curr, next);
            curr.skip_size.add(level,my_skip);
            prev.skip_size.add(level, this.size - my_skip + 1);
        }
        // Else update prev and curr as normal
        else{
            prev_skip = calculate_skip(prev, curr);
            my_skip = (prev.getSkip_size(level) + 1) - prev_skip;
            prev.setSkip_size(level, prev_skip);
            curr.skip_size.add(level, my_skip);

            if(my_skip == 0) curr.setSkip_size(level,1);
        }


    }

    /**
     * @param curr : The node we want to start counting from.
     * @param next : The node we will stop counting at.
     * Assumption  : curr and next are in the skip list.
     * @return the width of the skip from one node to another
     */
    private int calculate_skip(Node curr, Node next){
        int width = 0;

        // Count the width of the skip from curr
        while (curr != null && curr.key() < next.key()){
            curr = curr.getNext(0);
            width++;
        }

        return width;
    }
    /**
     * The method path goes through the path the method 'find' goes through and update each node skips according to the flag param.
     * @param curr : The node from which the code will update the skip width
     * @param level : The level from which the code will start the path
     * @param flag : The flag differentiates the operation of the function at insertion and deletion times.
     * **/
    public void updateHeight(Node curr, int level , boolean flag){
        int add;
        if(flag) add = 1;
        else     add = -1;

        // Go left as much as possible
        while(curr != null) {
            // Go in curr and add one each time
            for(int i = level + 1; i <= curr.height(); i++){
                curr.setSkip_size(i, curr.getSkip_size(i) + add);
            }
            level = curr.height();
            curr = curr.getPrev(level);
        }
    }

    public boolean delete(Node node) {
        for (int level = 0; level <= node.height(); ++level) {
            Node prev = node.getPrev(level);
            Node next = node.getNext(level);
            prev.setNext(level, next);
            next.setPrev(level, prev);
        }
        // Update the size of the skip list and each prev node of 'node' skips.
        this.size--;
        updateSkips(node);

        return true;
    }
    /**
     * The function will update the skip of the predecessor of 'node', and will call 'updateHeight' for the rest of the nodes.
     * @param node: The node that been deleted
     * **/
    public void updateSkips(Node node){
        Node prev;

        for(int i = 1; i <= node.height(); i++){
            prev = node.getPrev(i);
            int new_skip = prev.getSkip_size(i) + node.getSkip_size(i) - 1;
            prev.setSkip_size(i, new_skip);
        }
        
        updateHeight(node.getPrev(node.height()), node.height(), false);
    }
    public int predecessor(Node node) {
        return node.getPrev(0).key();
    }

    public int successor(Node node) {
        return node.getNext(0).key();
    }

    public int minimum() {
        if (head.getNext(0) == tail) {
            throw new NoSuchElementException("Empty Linked-List");
        }

        return head.getNext(0).key();
    }

    public int maximum() {
        if (tail.getPrev(0) == head) {
            throw new NoSuchElementException("Empty Linked-List");
        }

        return tail.getPrev(0).key();
    }

    private void levelToString(StringBuilder s, int level) {
        s.append("H    ");
        Node curr = head.getNext(level);

        while (curr != tail) {
            s.append(curr.key);
            s.append("    ");
            
            curr = curr.getNext(level);
        }

        s.append("T\n");
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();

        for (int level = head.height(); level >= 0; --level) {
            levelToString(str, level);
        }

        return str.toString();
    }

    public static class Node {
        final private List<Node> next;
        final private List<Node> prev;
        private int height;
        private ArrayList<Integer> skip_size; //new field
        final private int key;

        public Node(int key) {
            next = new ArrayList<>();
            prev = new ArrayList<>();
            this.height = -1;
            this.key = key;
            skip_size = new ArrayList<>(); // Initializing the new field
        }

        public Node getPrev(int level) {
            if (level > height) {
                throw new IllegalStateException("Fetching height higher than current node height");
            }

            return prev.get(level);
        }

        public Node getNext(int level) {
            if (level > height) {
                throw new IllegalStateException("Fetching height higher than current node height");
            }

            return next.get(level);
        }

        public void setNext(int level, Node next) {
            if (level > height) {
                throw new IllegalStateException("Fetching height higher than current node height");
            }

            this.next.set(level, next);
        }

        public void setPrev(int level, Node prev) {
            if (level > height) {
                throw new IllegalStateException("Fetching height higher than current node height");
            }

            this.prev.set(level, prev);
        }

        public void addLevel(Node next, Node prev) {
            ++height;
            this.next.add(next);
            this.prev.add(prev);
        }
        public int height() { return height; }
        public int key() { return key; }


        /*NEW METHODS*/

        public ArrayList<Integer> getSkip_size() {
            return skip_size;
        }

        public int getSkip_size(int level) {
            if (level > height) {
                throw new IllegalStateException("Fetching height higher than current node height");
            }

            return skip_size.get(level);
        }

        public void setSkip_size(int level,int newSize){
            if (level > height) {
                throw new IllegalStateException("Fetching height higher than current node height");
            }

            this.skip_size.set(level, newSize);
        }


    }

}
