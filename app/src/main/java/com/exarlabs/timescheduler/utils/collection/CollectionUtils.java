package com.bluerisc.eprivo.utils.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import rx.functions.Func1;

/**
 * This class is a utility class for Collection operations
 * Created by Arnold on 24.08.2016.
 */
public class CollectionUtils {

    // ------------------------------------------------------------------------
    // TYPES
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // STATIC FIELDS
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // STATIC METHODS
    // ------------------------------------------------------------------------

    /**
     * Removes the items from a collection which are not accomplishing the given condition
     *
     * @param collection        The collection from which the items will be deleted by a condition
     * @param conditionFunction A function what contains the condition by which the elements will be removed
     * @param <T>               This is the collection type (template)
     *
     * @return The original collection from which the items are deleted by the condition
     */
    public static <T> Collection<T> remove(Collection<T> collection, Func1<T, Boolean> conditionFunction) {

        for (Iterator<T> i = collection.iterator(); i.hasNext(); ) {
            if (conditionFunction.call(i.next())) {
                i.remove();
            }
        }

        return collection;
    }

    /**
     * Adding an item to a collection
     *
     * @param collection Collection to which the new item will be added
     * @param newItem    The new item which will be added to the collection
     * @param <T>        This is the collection type (template)
     *
     * @return The collection which contains the new item
     */
    public static <T> Collection<T> add(Collection<T> collection, T newItem) {
        collection.add(newItem);
        return collection;
    }

    /**
     * Adding an item to a collection by a condition
     *
     * @param collection        Collection to which the new item will be added
     * @param newItem           The new item which will be added to the collection
     * @param conditionFunction A function what contains the condition by which the elements will be added or not
     * @param <T>               This is the collection type (template)
     *
     * @return The collection which contains the new item
     */
    public static <T> Collection<T> addByCondition(Collection<T> collection, T newItem, Func1<T, Boolean> conditionFunction) {

        if (conditionFunction.call(newItem)) {
            collection.add(newItem);
        }

        return collection;
    }

    /**
     * Adding a collection to a collection
     *
     * @param addTo         The original collection to which will be added a collection
     * @param elementsToAdd This is the collection which will be added to the original collection
     * @param <T>           This is the collection type (template)
     *
     * @return The original collection which contains the other collection
     */
    public static <T> Collection<T> add(Collection<T> addTo, Collection<T> elementsToAdd) {

        for (T item : elementsToAdd) {
            addTo.add(item);
        }

        return addTo;
    }

    /**
     * Adding a collections to a collection
     *
     * @param addTo          The original collection to which will be added a collection
     * @param elementsToAdd1 This is the first collection which will be added to the original collection
     * @param elementsToAdd2 This is the first collection which will be added to the original collection
     * @param <T>            This is the collection type (template)
     *
     * @return The original collection which contains the other collection
     */
    public static <T> Collection<T> add(Collection<T> addTo, Collection<T> elementsToAdd1, Collection<T> elementsToAdd2) {

        for (T item : elementsToAdd1) {
            addTo.add(item);
        }

        for (T item : elementsToAdd2) {
            addTo.add(item);
        }

        return addTo;
    }

    /**
     * Sorts the hash map content based on values using the given comparator
     *
     * @param linkedHashMap the hash map which contain the data to be sorted
     * @param comparator    the comparator used to comare the elements
     * @param <K>           the key type
     * @param <V>           the value type
     */
    public static <K, V> void orderByValue(Map<K, V> linkedHashMap, final Comparator<? super V> comparator) {
        // Get the entries to a temp list
        List<Map.Entry<K, V>> entries = new ArrayList<>(linkedHashMap.entrySet());

        // Sort the entries using the given comparator.
        Collections.sort(entries, (lhs, rhs) -> comparator.compare(lhs.getValue(), rhs.getValue()));

        // Remove all the elements
        linkedHashMap.clear();

        // Put back again the sorted entries
        for (Map.Entry<K, V> e : entries) {
            linkedHashMap.put(e.getKey(), e.getValue());
        }
    }


    /**
     * @param map
     * @param value
     * @param <K>
     * @param <V>
     *
     * @return retuns the key assocaited with the given value. If the value is not found null is returned
     */
    public static <K, V> K getKey(Map<K, V> map, V value) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }

        return null;
    }

    public static <T> boolean contains(List<T> list, T item, Comparator<? super T> comparator) {
        for (T t : list) {
            if (comparator.compare(t, item) == 0) {
                return true;
            }
        }

        return false;
    }

    /**
     * @param collection
     * @param <T>
     *
     * @return true if the collection is null or has 0 size
     */
    public static <T> boolean isEmpty(Collection<T> collection) {
        return collection == null || collection.size() == 0;
    }

    /**
     * Returns the last #of items. If the list is empty, or it is null then it retusn an empty list
     *
     * @param batchSize
     * @param list
     *
     * @return
     */
    public static <T> List<T> getLast(int batchSize, List<T> list) {
        if (list == null) {
            return new ArrayList<>();
        }

        List<T> result = new ArrayList<>();

        int length = Math.max(0, list.size() - batchSize);

        for (int i = length; i < list.size(); i++) {
            result.add(list.get(i));
        }

        return result;
    }

    /**
     * Returns the last #of items. If the list is empty, or it is null then it retusn an empty list
     *
     * @param batchSize
     * @param list
     *
     * @return
     */
    public static <T> List<T> getFirst(int batchSize, List<T> list) {
        if (list == null) {
            return new ArrayList<>();
        }

        List<T> result = new ArrayList<>();

        int length = Math.min(list.size(), batchSize);

        for (int i = 0; i < length; i++) {
            result.add(list.get(i));
        }

        return result;
    }

    // ------------------------------------------------------------------------
    // FIELDS
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // CONSTRUCTORS
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // METHODS
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // GETTERS / SETTERS
    // ------------------------------------------------------------------------
}
