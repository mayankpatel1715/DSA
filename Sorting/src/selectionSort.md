# 🎯 Algorithm: Selection Sort

---

## 1. Description

`selectionSortLearn` sorts an integer array **in-place** in ascending order using the Selection Sort algorithm. Instead of bubbling elements one step at a time like Bubble Sort, Selection Sort takes a more **decisive** approach: on each pass, it **finds the maximum element** in the unsorted region and **directly places it at its final position** at the end of that region.

One swap per pass. No gradual drifting — just find the right element and snap it into place.

---

## 2. Input & Output

| Feature | Type | Description |
|---------|------|-------------|
| Input | `int[] arr` | A non-empty array of integers in any order. |
| Output | `void` | No return value. The original array is **mutated** (sorted in-place). |

---

## 3. Core Concept: What Does "Selection" Mean?

The name comes from the core action: **selecting** the correct element for each position.

Think of it as **filling seats from the back of a row:**

- You have `n` seats (indices 0 to n-1).
- You want the largest element in the last seat, the second-largest in the second-to-last seat, and so on.
- On each round, you **scan the entire unsorted region**, find the **maximum**, and **swap it** directly into the last unsorted seat.
- That seat is now permanently filled. You never touch it again.
- The unsorted region shrinks by one from the right each round.

This is fundamentally different from Bubble Sort. Bubble Sort moves elements one step at a time through swapping neighbors. Selection Sort **teleports** the maximum directly to its final position in one swap.

---

## 4. Thinking & Mental Model

### 🧠 The "Find the Tallest, Send Them to the Back" Model

Consider the array: `[29, 10, 14, 37, 13]`

The unsorted region starts as the full array. Each pass shrinks it by one from the right.

---

**Pass 0 (i=0):** `last = 4`  
Scan indices 0 to 4. Find the index of the maximum value.

```
arr = [29, 10, 14, 37, 13]
       ^           ^
     max=0      max=3 (37 is largest)

Swap arr[3]=37 with arr[last=4]=13
Result: [29, 10, 14, 13, 37]
                        ✅ 37 is in its final position
```

---

**Pass 1 (i=1):** `last = 3`  
Scan indices 0 to 3. Find max in unsorted region `[29, 10, 14, 13]`.

```
arr = [29, 10, 14, 13, | 37]
       ^
     max=0 (29 is largest in unsorted region)

Swap arr[0]=29 with arr[last=3]=13
Result: [13, 10, 14, 29, 37]
                    ✅ 29 is in its final position
```

---

**Pass 2 (i=2):** `last = 2`  
Scan indices 0 to 2. Find max in `[13, 10, 14]`.

```
arr = [13, 10, 14, | 29, 37]
               ^
             max=2 (14 is largest)

Swap arr[2]=14 with arr[last=2]=14  (same index — no real change)
Result: [13, 10, 14, 29, 37]
                ✅ 14 is in its final position
```

---

**Pass 3 (i=3):** `last = 1`  
Scan indices 0 to 1. Find max in `[13, 10]`.

```
arr = [13, 10, | 14, 29, 37]
       ^
     max=0 (13 is largest)

Swap arr[0]=13 with arr[last=1]=10
Result: [10, 13, 14, 29, 37]
             ✅ 13 is in its final position
```

**Final array:** `[10, 13, 14, 29, 37]` ✅  
The first element (10) falls into place automatically — no pass needed.

---

### 💡 Why `max = 0` as the Starting Point?

The inner loop initializes `max = 0` — this is not the maximum *value*, it is the **index** of the assumed maximum. We assume index `0` holds the largest value in the current unsorted region, then scan the rest to challenge that assumption. If any element at index `j` is larger than `arr[max]`, we update `max = j`.

This is the same "King of the Hill" pattern from the `largest` element algorithm — we're just using it to find the max index in a shrinking window instead of the full array.

---

## 5. `i < n-1` vs `i < n` — Deep Explanation

This is a critical question. Let's understand it from first principles.

### What does the outer loop actually do?

Each iteration of the outer loop `i` **places one element in its final sorted position** at index `last = arr.length - i - 1`.

For an array of `n` elements:

| Pass (`i`) | `last` index filled | Elements remaining unsorted |
|------------|---------------------|------------------------------|
| 0 | n-1 | n elements scanned |
| 1 | n-2 | n-1 elements scanned |
| 2 | n-3 | n-2 elements scanned |
| ... | ... | ... |
| n-2 | 1 | 2 elements scanned |
| n-1 | 0 | 1 element scanned |

### ✅ `i < n - 1` (correct)

The loop runs passes `i = 0, 1, 2, ..., n-2`.

After pass `i = n-2`, the element at index `1` is placed correctly. This means index `0` is the only remaining unsorted element — but with everyone else sorted, **it must already be the smallest**, so it is automatically in the right place. **No pass is needed for it.**

Result: `n - 1` passes sort all `n` elements. ✅

```
Array of 5 elements needs 4 passes (i = 0, 1, 2, 3).
After 4 passes, 4 elements are placed. The 1st element is correct by elimination.
```

### ⚠️ `i < n` (one extra pass — wasteful but not incorrect)

The loop runs passes `i = 0, 1, 2, ..., n-1`.

On the final extra pass (`i = n-1`):
- `last = arr.length - (n-1) - 1 = 0`
- The inner loop runs: `for (int j = 1; j <= 0; j++)` — **this condition is false immediately**, so the loop body **never executes**.
- `max` stays `0`, and we swap `arr[0]` with `arr[0]` — **a self-swap that does nothing**.

So `i < n` **does not produce wrong results**, but it wastes one full outer iteration doing absolutely nothing — a redundant self-swap on a fully sorted array.

### 🔍 Side-by-Side Summary

```
Array size n = 5

i < n - 1  → runs for i = 0, 1, 2, 3         (4 passes — correct and efficient)
i < n      → runs for i = 0, 1, 2, 3, 4       (5 passes — last one is wasted)

Pass i=4 with i < n:
  last = 5 - 4 - 1 = 0
  inner loop: for(j=1; j <= 0; j++) → never runs
  swap arr[0] with arr[0]  → no-op
  Wasted iteration. ❌ (not wrong, just unnecessary)
```

**Rule of thumb:** For any sort that fills `n` positions one at a time, you only ever need `n - 1` passes. The last element is always correct by elimination.

---

## 6. Algorithmic Approach

| Property | Value |
|----------|-------|
| **Strategy** | In-place, Comparison-based |
| **Paradigm** | Brute Force / Iterative |
| **Time Complexity (Best)** | $O(n^2)$ — no early exit possible |
| **Time Complexity (Average)** | $O(n^2)$ |
| **Time Complexity (Worst)** | $O(n^2)$ |
| **Space Complexity** | $O(1)$ — only `temp`, `max`, `last` used |
| **Stable?** | ❌ No — swapping can disrupt relative order of equal elements |
| **In-place?** | ✅ Yes |
| **Swaps per pass** | Exactly **1** (guaranteed) |

### Why Always $O(n^2)$?

Unlike Bubble Sort, there is **no early exit**. Even if the array is already sorted, the algorithm must still scan the entire unsorted region every pass to confirm the maximum. The number of comparisons is always:

$$
(n-1) + (n-2) + \ldots + 1 = \frac{n(n-1)}{2} \approx O(n^2)
$$

### ✅ One Key Advantage Over Bubble Sort: Fewer Swaps

Bubble Sort can perform up to $O(n^2)$ swaps in the worst case (one swap per comparison).  
Selection Sort performs **exactly $n - 1$ swaps** — one per pass, no matter what.

This makes Selection Sort preferable when **writes/swaps are expensive** (e.g., writing to flash memory or an external device) and reads are cheap.

---

## 7. Pseudocode

```plaintext
FUNCTION selectionSort(arr):
    SET n = length of arr

    FOR i FROM 0 TO n - 2:                        // n-1 passes total
        SET last = n - i - 1                       // Index of last unsorted position
        SET max = 0                                // Assume index 0 holds the max

        FOR j FROM 1 TO last:                      // Scan unsorted region
            IF arr[j] > arr[max] THEN              // Found a new maximum?
                SET max = j                        // Update max index
            END IF
        END FOR

        SWAP arr[max] WITH arr[last]               // Place max at its final position

    END FOR

END FUNCTION
```

---

## 8. Implementation Procedure

**Step 1 — Get the array length:**  
Store `arr.length` in `n`. Used to compute `last` and control the outer loop.

**Step 2 — Outer loop (`i` from `0` to `n-2`):**  
Each iteration handles one pass. `i` represents how many elements have already been placed at the tail. The loop runs `n-1` times because the last remaining element is always correct by elimination.

**Step 3 — Compute `last`:**  
`last = arr.length - i - 1` is the index of the **rightmost unsorted element** — the slot we need to fill this pass. On pass 0, `last = n-1` (the very end). On pass 1, `last = n-2`, and so on.

**Step 4 — Initialize `max = 0`:**  
This is an **index**, not a value. We assume the largest unsorted element is at index `0`. The inner loop will update this if a larger element is found.

**Step 5 — Inner loop (`j` from `1` to `last`):**  
Scans the entire unsorted region. For each `j`, if `arr[j]` is greater than `arr[max]`, update `max = j`. After the loop, `max` holds the index of the largest element in the unsorted region.

**Step 6 — Swap `arr[max]` with `arr[last]`:**  
The largest unsorted element is teleported directly to position `last`. This is the only swap in the entire pass. The three-step swap using `temp` ensures no data is lost.

---

## 9. Full Annotated Code

```java
static void selectionSortLearn(int[] arr) {
    int n = arr.length;

    // Outer loop: n-1 passes (last element falls into place automatically)
    for (int i = 0; i < n - 1; i++) {

        // 'last' is the final index of the current unsorted region
        // After this pass, arr[last] will hold the max of the unsorted region
        int last = arr.length - i - 1;

        // 'max' stores the INDEX (not value) of the current maximum
        // We assume index 0 is the maximum — inner loop will challenge this
        int max = 0;

        // Inner loop: scan from index 1 to last (inclusive)
        // j=0 is skipped because max already points there as the initial assumption
        for (int j = 1; j <= last; j++) {
            if (arr[max] < arr[j]) {    // Found a value larger than current max?
                max = j;               // Update max to point to the new champion
            }
        }

        // One swap: place the largest unsorted element at its final position
        int temp    = arr[last];
        arr[last]   = arr[max];
        arr[max]    = temp;
    }
}
```

---

## 10. Common Mistakes & Pitfalls

| Mistake | Why it's wrong | Fix |
|--------|----------------|-----|
| Using `max` as a value instead of an index | You lose track of where to swap | Always store `max` as an **index** |
| `i < n` instead of `i < n-1` | One wasted pass doing a self-swap | Use `i < n - 1` |
| Inner loop starts at `j = 0` | `arr[max]` vs `arr[0]` is the same comparison — wastes first iteration | Start at `j = 1` |
| Inner loop goes beyond `last` | Compares into already-sorted region, may misplace the max | Use `j <= last` as the bound |
| Forgetting `temp` in the swap | Overwrites a value and loses it permanently | Always use the 3-step swap |

---

## 11. Stability — Why Selection Sort is NOT Stable

A sort is **stable** if two elements with equal values maintain their original relative order after sorting.

Selection Sort **breaks stability** because of its long-distance swapping.

**Example:**

```
Input: [3a, 3b, 1]    (3a and 3b are equal in value, 'a'/'b' track original order)

Pass 0: last=2, scan full array → max=0 (3a is largest, found first)
Swap arr[0]=3a with arr[2]=1
Result: [1, 3b, 3a]

3a and 3b are now reversed! ❌ Stability broken.
```

The long-distance swap can jump over equal elements and flip their order.  
If stability matters, use **Insertion Sort** or **Merge Sort** instead.

---

## 12. When to Use Selection Sort

✅ **Good for:**
- **Minimizing swaps** — exactly `n-1` writes to the array (useful when write operations are costly)
- Small arrays where simplicity is valued over performance
- Teaching the concept of "find and place" sorting

❌ **Avoid for:**
- Large datasets — always $O(n^2)$, no best-case improvement
- When stability is required — use Insertion Sort or Merge Sort
- Nearly-sorted data — unlike Bubble/Insertion Sort, it gets no benefit

---

## 13. Comparison With Other Sorts

| Algorithm | Best | Average | Worst | Space | Stable | Swaps |
|-----------|------|---------|-------|-------|--------|-------|
| **Selection Sort** | $O(n^2)$ | $O(n^2)$ | $O(n^2)$ | $O(1)$ | ❌ | $O(n)$ |
| Bubble Sort | $O(n)$ | $O(n^2)$ | $O(n^2)$ | $O(1)$ | ✅ | $O(n^2)$ |
| Insertion Sort | $O(n)$ | $O(n^2)$ | $O(n^2)$ | $O(1)$ | ✅ | $O(n^2)$ |
| Merge Sort | $O(n \log n)$ | $O(n \log n)$ | $O(n \log n)$ | $O(n)$ | ✅ | N/A |
| Quick Sort | $O(n \log n)$ | $O(n \log n)$ | $O(n^2)$ | $O(\log n)$ | ❌ | $O(n \log n)$ |