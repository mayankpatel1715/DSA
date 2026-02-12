# 🫧 Algorithm: Bubble Sort

---

## 1. Description

`bubbleSortLearn` sorts an integer array **in-place** in ascending order using the Bubble Sort algorithm. It works by repeatedly comparing **adjacent elements** and swapping them if they are in the wrong order. After each full pass through the array, the largest unsorted element "bubbles up" to its correct position at the end.

An **early exit optimization** (`swapped` flag) is included: if an entire pass completes with zero swaps, the array is already sorted and the algorithm stops immediately — avoiding unnecessary work.

---

## 2. Input & Output

| Feature | Type | Description |
|---------|------|-------------|
| Input | `int[] arr` | A non-empty array of integers in any order. |
| Output | `void` | No return value. The original array is **mutated** (sorted in-place). |

> **Key Insight:** The array is modified directly in memory. There is no new array created. The caller's array is the one that gets sorted.

---

## 3. Core Concept: What Does "Bubble" Mean?

Imagine a row of numbered bubbles underwater. The **heaviest bubbles sink to the bottom, lightest rise to the top** — but in our case, the **largest numbers migrate to the right end** after each pass.

Think of it as a **tournament of neighbors:**

- You line up all the numbers.
- You walk from left to right, comparing every pair of neighbors: `(arr[0], arr[1])`, then `(arr[1], arr[2])`, and so on.
- Whenever the **left neighbor is bigger than the right neighbor**, they **swap places**.
- After one full walk (one pass), the **largest number in the unsorted region is guaranteed to be at its final position** on the right.
- You repeat this, but each time your walk gets **one step shorter** from the right — because the tail is already sorted.

---

## 4. Thinking & Mental Model

### 🧠 The "Pushing the Max to the End" Model

Consider the array: `[5, 3, 8, 1, 2]`

**Pass 1** — Walk from index 0 to index 3 (n-1-0 = 4, j goes from 1 to 4):

```
Compare arr[1]=3 and arr[0]=5   → 3 < 5  → SWAP  → [3, 5, 8, 1, 2]
Compare arr[2]=8 and arr[1]=5   → 8 > 5  → no swap → [3, 5, 8, 1, 2]
Compare arr[3]=1 and arr[2]=8   → 1 < 8  → SWAP  → [3, 5, 1, 8, 2]
Compare arr[4]=2 and arr[3]=8   → 2 < 8  → SWAP  → [3, 5, 1, 2, 8]
```

After Pass 1: `8` is at its final position. ✅

**Pass 2** — Walk from index 0 to index 2 (only up to `n-1-1 = 3`, j goes from 1 to 3):

```
Compare arr[1]=5 and arr[0]=3   → 5 > 3  → no swap → [3, 5, 1, 2, 8]
Compare arr[2]=1 and arr[1]=5   → 1 < 5  → SWAP  → [3, 1, 5, 2, 8]
Compare arr[3]=2 and arr[2]=5   → 2 < 5  → SWAP  → [3, 1, 2, 5, 8]
```

After Pass 2: `5` is at its final position. ✅

...and so on until fully sorted: `[1, 2, 3, 5, 8]`

### 💡 The Shrinking Window

After each pass `i`, the last `i+1` elements are **already sorted and frozen**. So the inner loop only needs to run up to index `n - 1 - i`. This is why the inner loop bound shrinks each round.

```
Pass 0: compare up to index n-1  (full array)
Pass 1: compare up to index n-2  (last 1 frozen)
Pass 2: compare up to index n-3  (last 2 frozen)
...
```

### ⚡ The `swapped` Flag Optimization

**What problem does it solve?**

On a nearly-sorted or already-sorted array, standard Bubble Sort still runs all `O(n²)` comparisons — even when no swaps are needed.

The `swapped` flag solves this:

- Before each outer pass, `swapped` is set to `false`.
- If **any swap happens** during the inner loop, `swapped` is flipped to `true`.
- After the inner loop, if `swapped` is **still false** — no elements were out of order, meaning the array is sorted. We `break` immediately.

> ⚠️ **Bug Note:** In this implementation, `swapped` is initialized to `false` **before the outer loop** but is never **reset to `false` at the start of each iteration**. This means once `swapped` becomes `true`, it stays `true` forever and the early exit never triggers after the first swap occurs. The fix is to move `boolean swapped = false;` **inside** the outer `for` loop.

**Corrected placement:**
```java
for (int i = 0; i < n - 1; i++) {
    boolean swapped = false;   // ✅ Reset at the start of EVERY pass
    for (int j = 1; j <= n - 1 - i; j++) {
        ...
    }
    if (!swapped) break;
}
```

---

## 5. Input & Output Walkthrough

**Input:** `arr = [64, 34, 25, 12, 22, 11, 90]`

| Pass | Array State After Pass | Max Bubbled |
|------|------------------------|-------------|
| 0 | `[34, 25, 12, 22, 11, 64, 90]` | 90 |
| 1 | `[25, 12, 22, 11, 34, 64, 90]` | 64 |
| 2 | `[12, 22, 11, 25, 34, 64, 90]` | 34 |
| 3 | `[12, 11, 22, 25, 34, 64, 90]` | 25 |
| 4 | `[11, 12, 22, 25, 34, 64, 90]` | 22 |
| 5 | `[11, 12, 22, 25, 34, 64, 90]` | (no swaps → break) |

**Output (mutated array):** `[11, 12, 22, 25, 34, 64, 90]` ✅

---

## 6. Algorithmic Approach

| Property | Value |
|----------|-------|
| **Strategy** | Comparison-based, In-place, Stable Sort |
| **Paradigm** | Brute Force / Iterative |
| **Time Complexity (Best)** | $O(n)$ — already sorted array (with `swapped` fix) |
| **Time Complexity (Average)** | $O(n^2)$ |
| **Time Complexity (Worst)** | $O(n^2)$ — reverse sorted array |
| **Space Complexity** | $O(1)$ — only `temp`, `i`, `j`, `swapped` used |
| **Stable?** | ✅ Yes — equal elements never swap, so relative order is preserved |
| **In-place?** | ✅ Yes — no extra array is created |

### Why $O(n^2)$?

The outer loop runs `n - 1` times. The inner loop runs `n - 1 - i` times for each outer iteration. Total comparisons:

$$
(n-1) + (n-2) + (n-3) + \ldots + 1 = \frac{n(n-1)}{2} \approx O(n^2)
$$

---

## 7. Pseudocode

```plaintext
FUNCTION bubbleSort(arr):
    SET n = length of arr

    FOR i FROM 0 TO n - 2:                     // Outer pass: n-1 passes total
        SET swapped = false                     // ✅ Reset flag each pass

        FOR j FROM 1 TO n - 1 - i:             // Inner scan: shrinks each pass
            IF arr[j] < arr[j - 1] THEN        // Out-of-order neighbors?
                SWAP arr[j] AND arr[j - 1]     // Push larger one right
                SET swapped = true             // Mark that a swap occurred
            END IF
        END FOR

        IF swapped == false THEN               // No swaps → already sorted
            BREAK                              // Early exit
        END IF
    END FOR

END FUNCTION
```

---

## 8. Implementation Procedure

**Step 1 — Get the array length:**
Store `arr.length` in `n`. This is used to control both loop bounds and avoid repeated method calls.

**Step 2 — Outer loop (`i` from `0` to `n-2`):**
This loop controls how many passes we make. After pass `i`, the last `i+1` elements are in their final sorted positions. We only need `n-1` passes in the worst case (the last element automatically falls into place).

**Step 3 — Initialize `swapped = false` (should be inside outer loop):**
This flag tracks whether any adjacent pair was swapped during a pass. If no swap happens, the array is sorted and we can stop early.

**Step 4 — Inner loop (`j` from `1` to `n-1-i`):**
This loop scans the unsorted portion of the array. `j` starts at `1` so we can always look back at `arr[j-1]`. The upper bound `n-1-i` shrinks each pass because the rightmost `i` elements are already sorted.

**Step 5 — Compare and swap:**
If `arr[j] < arr[j-1]`, the elements are out of order. We perform a classic **three-step swap** using a temporary variable:
```java
int temp  = arr[j];
arr[j]    = arr[j-1];
arr[j-1]  = temp;
```
Then set `swapped = true`.

**Step 6 — Early exit check:**
After the inner loop completes, check `if (!swapped)`. If no swap occurred during the entire pass, the array was already in order and we break out of the outer loop.

---

## 9. Full Annotated Code

```java
static void bubbleSortLearn(int[] arr) {
    int n = arr.length;

    // ⚠️ BUG: swapped should be declared INSIDE the outer loop (see Section 4)
    boolean swapped = false;

    // Outer loop: controls the number of passes
    // After pass i, the last (i+1) elements are sorted
    for (int i = 0; i < n - 1; i++) {

        // Inner loop: compares adjacent pairs in the unsorted region
        // j starts at 1 so arr[j-1] is always valid
        // Upper bound shrinks each pass (n-1-i) because tail is frozen
        for (int j = 1; j <= n - 1 - i; j++) {

            // If left neighbor is greater than right neighbor → out of order
            if (arr[j] < arr[j - 1]) {

                // Three-step swap (no XOR trick — readable and safe)
                int temp   = arr[j];
                arr[j]     = arr[j - 1];
                arr[j - 1] = temp;

                swapped = true;  // A swap occurred in this pass
            }
        }

        // Optimization: if no swaps in this pass, array is already sorted
        if (!swapped) break;
    }
}
```

---

## 10. Common Mistakes & Pitfalls

| Mistake | Why it's wrong | Fix |
|--------|----------------|-----|
| `swapped` declared outside outer loop | Flag never resets; early exit never works after first swap | Move declaration inside outer loop |
| Inner loop goes up to `n-1` always | Rechecks already-sorted tail, wastes time | Use `n - 1 - i` as upper bound |
| Starting inner loop at `j = 0` | Accessing `arr[j-1]` = `arr[-1]` → `ArrayIndexOutOfBoundsException` | Start at `j = 1` |
| Forgetting `temp` during swap | Directly overwriting loses a value | Always use 3-step swap with `temp` |

---

## 11. When to Use Bubble Sort

✅ **Good for:**
- Teaching and learning sorting concepts
- Very small arrays (< 10 elements) where simplicity matters
- Nearly-sorted arrays (best case $O(n)$ with the `swapped` fix)
- Situations where **stable sorting** is required and simplicity is preferred

❌ **Avoid for:**
- Large datasets — $O(n^2)$ is too slow
- Performance-critical applications — use `Arrays.sort()` (Timsort, $O(n \log n)$) instead

---

## 12. Comparison With Other Sorts

| Algorithm       | Best          | Average       | Worst         | Space       | Stable |
|-----------------|---------------|---------------|---------------|-------------|--------|
| **Bubble Sort** | $O(n)$        | $O(n^2)$      | $O(n^2)$      | $O(1)$      | ✅      |
| Selection Sort  | $O(n^2)$      | $O(n^2)$      | $O(n^2)$      | $O(1)$      | ❌      |
| Insertion Sort  | $O(n)$        | $O(n^2)$      | $O(n^2)$      | $O(1)$      | ✅      |
| Merge Sort      | $O(n \log n)$ | $O(n \log n)$ | $O(n \log n)$ | $O(n)$      | ✅      |
| Quick Sort      | $O(n \log n)$ | $O(n \log n)$ | $O(n^2)$      | $O(\log n)$ | ❌      |