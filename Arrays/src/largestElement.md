# 🚀 Algorithm: Finding the Largest Element in an Array

## 1. Description

The `largest` function identifies the maximum numerical value within an integer array. It operates by performing a single pass through the data, maintaining a "champion" variable that stores the highest value encountered at any given point.

## 2. Input & Output

| Feature | Type        | Description                                     |
|---------|-------------|-------------------------------------------------|
| Input   | `int[] arr` | A non-empty array of integers.                  |
| Output  | `int`       | The maximum integer value present in the array. |

## 3. Thinking & Mental Model

The logic follows a "King of the Hill" approach:

* **Initial Assumption**: We assume the first person in line (the first element) is the tallest.
* **The Comparison**: We walk down the line. If we find someone taller than our current "king," that new person becomes the king.
* **The Result**: By the time we reach the end of the line, the current king is guaranteed to be the tallest overall.
* **Constraint Note**: This implementation assumes the array has at least one element. If the array were empty, `arr[0]` would throw an error.

## 4. Algorithmic Approach

* **Strategy**: Linear Scan (Iterative).
* **Time Complexity**: $O(n)$, where $n$ is the number of elements in the array. We must visit every element exactly once.
* **Space Complexity**: $O(1)$ (Constant Space). We only store one integer (`max`) regardless of how large the input array grows.

## 5. Pseudocode

```plaintext
FUNCTION largest(arr):
    SET max = arr[0]                // Initialize with the first element
    
    FOR EACH element IN arr:        // Traverse the entire collection
        IF element > max THEN       // Comparison step
            SET max = element       // Update step
        END IF
    END FOR
    
    RETURN max                      // Result
END FUNCTION
```

## 6. Implementation Procedure

1. **Initialization**: The variable `max` is initialized to the value at index 0. This ensures the initial comparison value is actually a member of the set.
2. **Iteration**: A `for-each` loop is used to traverse the array from the first element to the last.
3. **Comparison Logic**: Inside the loop, the current `element` is compared against `max`. If the `element` is greater, the `max` variable is updated to reflect this new value.
4. **Completion**: Once the loop terminates, the function returns the value stored in `max`.