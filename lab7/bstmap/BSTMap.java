package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {

    private class BSTNode {
        BSTNode left, right;
        K key; // for search
        V value; // storage

        BSTNode(K k, V v) {
            key = k;
            value = v;
        }

    }

    private BSTNode root;
    private int size;

    public BSTMap() {
        root = null;
    }

    @Override
    public void clear() {
        size = 0;
        root = null;
    }

    @Override
    public boolean containsKey(K key) {
        BSTNode node = root;
        while (node != null) {
            int cmp = node.key.compareTo(key);
            if (cmp == 0) {
                return true;
            } else if (cmp < 0) {
                node = node.left;
            } else {
                node = node.right;
            }
        }

        return false;
    }

    @Override
    public V get(K key) {
        BSTNode node = root;
        while (node != null) {
            int cmp = node.key.compareTo(key);
            if (cmp == 0) {
                return node.value;
            } else if (cmp < 0) {
                node = node.left;
            } else {
                node = node.right;
            }
        }

        return null;
    }

    @Override
    public int size() {
        return size;
    }

    private BSTNode insert(BSTNode node, K key, V value) {
        if (node == null) {
            size++;
            return new BSTNode(key, value);
        } else if (node.key.compareTo(key) < 0) {
            node.left = insert(node.left, key, value);
        } else if (node.key.compareTo(key) > 0) {
            node.right = insert(node.right, key, value);
        }
        return node;
    }

    @Override
    public void put(K key, V value) {
        root = insert(root, key, value);
    }

    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    private BSTNode getRightMost(BSTNode node) {
        if (node == null) {
            return null;
        }
        while (node.right != null) {
            node = node.right;
        }
        return node;
    }

    private BSTNode removeHelper(BSTNode node, K key) {
        if (node == null) {
            return null;
        }
        int cmp = node.key.compareTo(key);
        if (cmp == 0) {
            if (node.left == null) {
                return node.right;
            }
            BSTNode maxInLeft = getRightMost(node.left);
            node.key = maxInLeft.key;
            node.value = maxInLeft.value;
            node.left = removeHelper(node.left, maxInLeft.key);
        } else if (cmp < 0) {
            node.left = removeHelper(node.left, key);
        } else {
            node.right = removeHelper(node.right, key);
        }
        return node;
    }

    @Override
    public V remove(K key) {
        if (containsKey(key)) {
            V r = get(key);
            root = removeHelper(root, key);
            size--;
            return r;
        } else {
            return null;
        }
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }
}
