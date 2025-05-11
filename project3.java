
import java.io.*;
//import java.lang.classfile.components.ClassPrinter;
import java.nio.*;
import java.util.*;


public class Project3 {

    //initialize global constants
    private static final int BLOCK_SIZE = 512;
    private static final String MAGIC_NUM = "4348PRJ3";
    private static final int MIN_DEG = 10;
    private static final int MAX_KEYS = 2*(MIN_DEG - 1);
    private static final int MAX_CHILDREN = MAX_KEYS + 1;


    public static void main(String[] args) throws IOException {

        if (args.length == 0) {
            printUsage();
            return;
        }

        //get the command
        String command = args[0]; 
        
        if (command.equals("create")) {
            //check if command is correct
            if (args.length < 2) {
                System.out.println("Invalid command arguments");
                return;
            }

            //execute function
            create(args[1]);

        } else if (command.equals("insert")) {
            //check if command is correct
            if (args.length < 4) {
                System.out.println("Invalid command arguments");
                return;
            }

            //make sure key and value are correct integers
            try {
                long key = Long.parseUnsignedLong(args[2]);
                long val = Long.parseUnsignedLong(args[3]);
                insert(args[1], key, val);
            } catch (NumberFormatException e) {
                System.out.println("Error: Key and Value must be Integers.");
                return;
            }


        } else if (command.equals("search")) {
            //check if command is correct
            if (args.length < 3) {
                System.out.println("Invalid command arguments");
                return;
            }

            //Make sure key is a correct integer
            try {
                long key = Long.parseUnsignedLong(args[2]);
                search(args[1], key);

            } catch (NumberFormatException e) {
                System.out.println("Error:Key must be an integer.");
                return;
            }


        } else if (command.equals("load")) {
            //check if command is correct
            if (args.length < 3) {
                System.out.println("Invalid command arguments");
                return;
            }

            //execute function
            load(args[1], args[2]);

        } else if (command.equals("print")) {
            //check if command is correct
            if (args.length < 2) {
                System.out.println("Invalid command arguments");
                return;
            }

            //execute function
            print(args[1]);



            
        } else if (command.equals("extract")) {
            //check if command is correct
            if (args.length < 3) {
                System.out.println("Invalid command arguments");
                return;
            }

            //execute function
            extract(args[1], args[2]);

        }
    }

    static class Header {
        byte[] magicNum;
        long rootID;
        long nextBlockID;

        public Header() {
            this.magicNum = MAGIC_NUM.getBytes();
            this.rootID = 0;
            this.nextBlockID = 1;
        }

        public ByteBuffer toByte() {
            ByteBuffer buffer = ByteBuffer.allocate(BLOCK_SIZE);
            buffer.order(ByteOrder.BIG_ENDIAN);

            //convert magic number to 8 bytes
            buffer.put(magicNum);
            for (int i = magicNum.length; i < 8; i++) {
                buffer.put((byte)0);
            }

            buffer.putLong(rootID);
            buffer.putLong(nextBlockID);

            //go to beginning of buffer
            buffer.rewind();
            return buffer;

        }

        public static Header fromByte(ByteBuffer buffer) throws IOException {
            buffer.rewind();
            buffer.order(ByteOrder.BIG_ENDIAN);

            Header header = new Header();

            //check magic number
            byte[] magicNumBytes = new byte[8];
            buffer.get(magicNumBytes);
            String magic = new String(magicNumBytes).trim();

            //if wrong magic num throw error
            if (!magic.equals(MAGIC_NUM)) {
                throw new IOException("Error: Invalid index file. The Magic Number does not match");
            }

            header.rootID = buffer.getLong();
            header.nextBlockID = buffer.getLong();

            return header;

        }
        

    }

    static class Node {
        long blockID;
        long parentID;
        int numKeys;
        long[] keys;
        long[] vals;
        long[] children;

        public Node(long blockID, long parentID) {
            this.blockID = blockID;
            this.parentID = parentID;
            this.numKeys = 0;
            this.keys = new long[MAX_KEYS];
            this.vals = new long[MAX_KEYS];
            this.children = new long[MAX_CHILDREN];

        }

        public boolean leaf() {
            for (int i = 0; i < MAX_CHILDREN; i++) {
                if (children[i] != 0) {
                    return false;
                }
            }
            return true;
        }

        public boolean full() {
            return numKeys == MAX_KEYS;
        }

        public ByteBuffer toByte() {
            ByteBuffer buffer = ByteBuffer.allocate(BLOCK_SIZE);
            buffer.order(ByteOrder.BIG_ENDIAN);

            buffer.putLong(blockID);
            buffer.putLong(parentID);
            buffer.putLong(numKeys);

            //write keys
            for (int i = 0; i < MAX_KEYS; i++) {
                buffer.putLong(keys[i]);
            }

            //write values
            for (int i = 0; i < MAX_KEYS; i++) {
                buffer.putLong(vals[i]);
            }

            //write children
            for (int i = 0; i < MAX_KEYS; i++) {
                buffer.putLong(children[i]);
            }

            //go back to beginning of buffer
            buffer.rewind();
            return buffer;
        }

        public static Node fromByte(ByteBuffer buffer) {
            //go to beginning of buffer
            buffer.rewind();
            
            //get big endian order
            buffer.order(ByteOrder.BIG_ENDIAN);

            //get the metadata 
            long blockID = buffer.getLong();
            long parentID = buffer.getLong();
            long numKeys = buffer.getLong();

            //create a node
            Node node = new Node(blockID, parentID);
            node.numKeys = (int)numKeys;

            //read keys
            for (int i = 0; i < MAX_KEYS; i++) {
                node.keys[i] = buffer.getLong();
            }

            //read values
            for (int i = 0; i < MAX_KEYS; i++) {
                node.vals[i] = buffer.getLong();
            }

            //read children
            for (int i = 0; i < MAX_KEYS; i++) {
                node.children[i] = buffer.getLong();
            }

            return node;

        }


    }

    static class BTree {
        private RandomAccessFile file;
        private Header header;

        //store 3 nodes in memory at once
        private Map<Long, Node> nodeCache;
        private final int MAX_CACHE_SIZE = 3;

        public BTree(String filename) throws IOException {
            //create a file with the filename
            this.file = new RandomAccessFile(filename, "rw");
            //create node cache
            this.nodeCache = new LinkedHashMap<Long, Node>(MAX_CACHE_SIZE + 1, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<Long, Node> eldest) {
                    boolean shouldRemove = size() > MAX_CACHE_SIZE;
                    if (shouldRemove) {
                        try {
                            //write node to disk before removing from cache
                            writeNode(eldest.getValue());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    return shouldRemove;
                }
            };

            //read header
            if (file.length() >= BLOCK_SIZE) {
                ByteBuffer buffer = ByteBuffer.allocate(BLOCK_SIZE);
                file.getChannel().read(buffer, 0);
                this.header = Header.fromByte(buffer);
            } else {
                //there is no header so create a header and write it to file
                this.header = new Header();
                writeHeader();
            }

        }

        public void close() throws IOException {
            //get all nodes from cache
            Node[] cacheNodes = nodeCache.values().toArray(new Node[0]);

            //write all nodes in array
            for (int i = 0; i < cacheNodes.length; i++) {
                Node currNode = cacheNodes[i];
                writeNode(currNode);
            } 
        }

        private void writeHeader() throws IOException {
            //convert buffer
            ByteBuffer buffer = header.toByte();
            //write to file
            file.getChannel().write(buffer, 0);
        }

        private void writeNode(Node node) throws IOException {
            //convert buffer
            ByteBuffer buffer = node.toByte();
            //write to file
            file.getChannel().write(buffer, node.blockID * BLOCK_SIZE);

        }

        private Node readNode (long blockID) throws IOException {
            //check if node is in cache
            if (nodeCache.containsKey(blockID)) {
                return nodeCache.get(blockID);
            }

            //if not, read node from disk
            ByteBuffer buffer = ByteBuffer.allocate(BLOCK_SIZE);
            file.getChannel().read(buffer, blockID * BLOCK_SIZE);
            Node node = Node.fromByte(buffer);

            //add node to cache
            nodeCache.put(blockID, node);

            return node;

        }

        public boolean insert (long key, long val) throws IOException {
            //if tree is empty, create a root
            if (header.rootID == 0) {
                //assign header parameters
                Node root = new Node (header.nextBlockID, 0);
                header.rootID = header.nextBlockID;
                header.nextBlockID++;

                root.keys[0] = key;
                root.vals[0] = val;
                root.numKeys = 1;

                //add root to cache
                nodeCache.put(root.blockID, root);

                //write header to file
                writeHeader();

                return true;

            }

            //create root node
            Node root = readNode(header.rootID);

            //if root is full, split it
            if (root.full()) {
                //create a new root
                Node newRoot = new Node(header.nextBlockID, 0);
                header.nextBlockID++;

                //make old root a child of new root
                newRoot.children[0] = root.blockID;

                //make new root a parent of old root
                root.parentID = newRoot.blockID;

                //split old root
                splitNode(newRoot, 0, root);

                //update header
                header.rootID = newRoot.blockID;

                //add new root to cache
                nodeCache.put(newRoot.blockID, newRoot);

                //insert into new root
                insertVal(newRoot, key, val);

            } else {
                //insert into root
                insertVal(root, key, val);
            }

            //write header to file
            writeHeader();
            return true;


        }

        private void splitNode(Node parent, int index, Node child) throws IOException {
            //create a new node
            Node newNode = new Node(header.nextBlockID, parent.blockID);
            header.nextBlockID++;

            //put right half of child's data into the new node
            for (int i = 0; i < MIN_DEG - 1; i++) {
                newNode.keys[i] = child.keys[i + MIN_DEG];
                newNode.vals[i] = child.vals[i + MIN_DEG];

                //clear the data from the child
                child.keys[i + MIN_DEG] = 0;
                child.vals[i + MIN_DEG] = 0;

            }

            //put right half of child's children into new node
            if (!child.leaf()) {
                for (int i = 0; i < MIN_DEG; i++) {
                    newNode.children[i] = child.children[i + MIN_DEG];
                    child.children[i + MIN_DEG] = 0;

                    //change parent of the children
                    if (newNode.children[i] != 0) {
                        Node childNode = readNode(newNode.children[i]);
                        childNode.parentID = newNode.blockID;
                    }
                }
            }

            //update num keys
            newNode.numKeys = MIN_DEG - 1;
            child.numKeys = MIN_DEG - 1;


            //add new child to parent
            for (int i = parent.numKeys; i >= index + 1; i--) {
                parent.children[i + 1] = parent.children[i];
            }
            parent.children[index + 1] = newNode.blockID;

            //add key from child
            for (int i = parent.numKeys - 1; i >= index; i--) {
                parent.keys[i + 1] = parent.keys[i];
                parent.vals[i + 1] = parent.vals[i];
            }

            //copy middle key and val from child to parent
            parent.keys[index] = child.keys[MIN_DEG - 1];
            parent.vals[index] = child.vals[MIN_DEG - 1];

            //empty data in child
            child.keys[MIN_DEG - 1] = 0;
            child.vals[MIN_DEG - 1] = 0;

            //update num keys in parent
            parent.numKeys++;

            //add new node to cache
            nodeCache.put(newNode.blockID, newNode);

        }

        private void insertVal (Node node, long key, long val) throws IOException {
            int i = node.numKeys - 1;

            if (node.leaf()) {
                //find insert location
                while (i >= 0 && key < node.keys[i]) {
                    node.keys[i + 1] = node.keys[i];
                    node.vals[i + 1] = node.vals[i];
                    i--;
                }

                //if key already exists, update value
                if (i >= 0 && key == node.keys[i]) {
                    node.vals[i] = val;
                    return;
                }

                //insert key and val
                node.keys[i + 1] = key;
                node.vals[i + 1] = val;
                node.numKeys++;
            } else {
                //find location of child
                while (i >= 0 && key < node.keys[i]) {
                    i--;
                }
                i++;

                //check if key already exists
                if (i > 0 && i < node.numKeys && key == node.keys[i - 1]) {
                    node.vals[i - 1] = val;
                    return;
                }

                //read child
                Node child = readNode(node.children[i]);

                //if child is full, split it
                if (child.full()) {
                    splitNode(node, i, child);

                    //after splitting check if key goes to new child
                    if (key > node.keys[i]) {
                        i++;
                        //get new child
                        child = readNode(node.children[i]);
                    }
                }

                //insert into child
                insertVal(child, key, val);
            }
        }

        public long search(long key) throws IOException {
            //check if tree is empty
            if (header.rootID == 0) {
                return -1; 
            }

            return searchNode(header.rootID, key);
        }

        private long searchNode(long nodeID, long key) throws IOException {
            Node node = readNode(nodeID);

            int i = 0;
            while(i < node.numKeys && key > node.keys[i]) {
                i++;
            }

            //check if key is found
            if (i < node.numKeys && key == node.keys[i]) {
                //get value
                return node.vals[i];
            }

            //if node is a leaf, key is not in tree
            if (node.leaf()) {
                return -1;
            }

            //search the child
            return searchNode(node.children[i], key);
        }
        

        public List<KeyValPair> getAllRecords() throws IOException {
            //create arraylist to store all key value pairs
            List <KeyValPair> records = new ArrayList<>();

            //add key value pairs to the list
            if (header.rootID != 0) {
                collectRecords(header.rootID, records);
            }

            return records;
        }

        private void collectRecords(long nodeID, List<KeyValPair> records) throws IOException {
            Node node = readNode(nodeID);
            int i = 0;

            //Recursively traverse the tree
            if (!node.leaf()) {
                for (i = 0; i < node.numKeys; i++) {
                    if (node.children[i] != 0) {
                        collectRecords(node.children[i], records);
                    }
                    records.add(new KeyValPair(node.keys[i], node.vals[i]));
                }

                //get last child
                if (node.children[i] != 0) {
                    collectRecords(node.children[i], records);
                }
            } else {
                //if node is a leaf just collect records from this leaf
                for (i = 0; i < node.numKeys; i++) {
                    records.add(new KeyValPair(node.keys[i], node.vals[i]));
                }
            }

        }


        static class KeyValPair {
            public long key;
            public long val;

            public KeyValPair(long key, long val) {
                this.key = key;
                this.val = val;
            }
        }

        

        
    }



    private static void printUsage() {
        System.out.println("    create INDEX_FILE                   Create new index");
        System.out.println("    insert INDEX_FILE KEY VALUE         Insert a new key/value pair into current index");
        System.out.println("    search INDEX_FILE KEY               Search for a key in current index");
        System.out.println("    load INDEX_FILE CSV_FILE            Insert key/value pairs from a file into current index");
        System.out.println("    print INDEX_FILE                    Print all key/value pairs in current index in key order");
        System.out.println("    extract INDEX_FILE SAVE_FILE        Save all key/value pairs in current index into a file");

    }

    private static void create(String filename) throws IOException {
        //check if file already exists
        File file = new File(filename); 
        if (file.exists()) {
            System.out.println("Error: File " + filename + " already exists.");
            return;
        }

        //create a new B-Tree
        BTree btree = new BTree(filename);
        btree.close();

        System.out.println("Index file " + filename + " created successfully.");

    }

    private static void insert(String filename, long key, long val) throws IOException {
        //make sure file exists
        File file = new File(filename);
        if (!file.exists()) {
            System.out.println("Error: File " + filename + " does not exist.");
            return;
        }

        //insert into BTree
        try {
            BTree btree = new BTree(filename);

            //check if key already exists
            Long currVal = btree.search(key);
            if (currVal != null) {
                //print statement and skip this value
                System.out.println("Key " + key + " already exists.");
            } else {
                btree.insert(key, val);
                System.out.println("Key " + key + " with value " + val + " inserted successfully.");
            }
            
            btree.close();
        } catch (IOException e) {
            System.out.println("Error: Invalid index file or I/O error.");
            throw e;
        }
    }

    private static void search(String filename, long key) throws IOException {
        //make sure file exists
        File file = new File(filename);
        if (!file.exists()) {
            System.out.println("Error: File " + filename + " does not exist.");
            return;
        }

        //search btree
        try {
            BTree btree = new BTree(filename);
            long val = btree.search(key);
            btree.close();

            if (val != -1) {
                System.out.println("Value '" + val + "' is at key '" + key + "'");
            } else {
                System.out.println("Key " + key + " not found.");
            }

        } catch (IOException e) {
            System.out.println("Invalid index file or I/O error.");
            throw e;

        }
    }

    private static void load(String indexFilename, String csvFilename) throws IOException {
        //make sure index file exists
        File indexFile = new File(indexFilename);
        if (!indexFile.exists()) {
            System.out.println("Error: Index file '" + indexFilename + "' does not exist.");
            return;
        }

        //make sure csv file exists
        File csvFile = new File(csvFilename);
        if (!csvFile.exists()) {
            System.out.println("Error: Index file '" + csvFilename + "' does not exist.");
            return;
        }

        try {
            BTree btree = new BTree(indexFilename);
            int count = 0;

            //to read from csv file
            BufferedReader reader = new BufferedReader(new FileReader(csvFilename));
            String line;

            //read from file
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    String[] params = line.split(",");
                    if (params.length >= 2) {
                        //make sure key and value are correct integers
                        try {
                            long key = Long.parseUnsignedLong(params[0].trim());
                            long val = Long.parseUnsignedLong(params[1].trim());

                            //check if key already exists
                            Long currVal = btree.search(key);
                            if (currVal != null) {
                                //print statement and skip this value
                                System.out.println("Key " + key + " already exists.");
                            } else {
                                btree.insert(key, val);
                                count++;
                            }

                        } catch (NumberFormatException e) {
                            System.out.println("Warning: Invalid line is being skipped: " + line);
                        }
                    }
                }
            }

            reader.close();
            btree.close();

            System.out.println("Loaded " + count + " key/value pairs from " + csvFilename + " successfully.");

        } catch (IOException e) {
            System.out.println("Error: Invalid index file, CSV file, or I/O error.");
            throw e;
        }

    }

    private static void print(String filename) throws IOException {
        //make sure file exists
        File file = new File(filename);
        if (!file.exists()) {
            System.out.println("Error: File '" + filename + "' does not exist.");
            return;
        }

        try {
            BTree btree = new BTree(filename);
            List<BTree.KeyValPair> records = btree.getAllRecords();
            btree.close();

            if (records.isEmpty()) {
                System.out.println("Index is empty.");
            } else {
                for (int i = 0; i < records.size(); i++) {
                    BTree.KeyValPair pair = records.get(i);
                    System.out.println("Key = " + pair.key + ", Value = " + pair.val);
                }
                
            }

        } catch (IOException e) {
            System.out.println("Error: Invalid index file or I/O error.");
            throw e;
        }


    }

    private static void extract(String indexFilename, String csvFilename) throws IOException {
        //make sure index file exists
        File indexFile = new File(indexFilename);
        if (!indexFile.exists()) {
            System.out.println("Error: Index file '" + indexFilename + "' does not exist.");
            return;
        }

        //see if csv file already exists
        File csvFile = new File(csvFilename);
        if (csvFile.exists()) {
            System.out.println("Error: CSV file " + csvFilename + " already exists.");
            return;
        }

        try {
            BTree btree = new BTree(indexFilename);
            List<BTree.KeyValPair> records = btree.getAllRecords();
            btree.close();

            BufferedWriter writer = new BufferedWriter(new FileWriter(csvFilename));
            for (int i = 0; i < records.size(); i++) {
                BTree.KeyValPair pair = records.get(i);
                writer.write(pair.key + ", " + pair.val);
                writer.newLine();
            } 

            writer.close();

            System.out.println("Extracted " + records.size() + " key/value pairs from " + indexFilename + " to " + csvFilename + ".");


        } catch (IOException e) {
            System.out.println("Error: Invalid index file or I/O error.");
            throw e;
        }
    }

}