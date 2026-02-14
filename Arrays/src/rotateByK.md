# 🔄 Algorithm: Rotate Array Right by K Positions

---

## 1. Description

`rotate` shifts every element in an integer array **k positions to the right**. Elements that fall off the right end wrap around to the front.

```
Input:  [1, 2, 3, 4, 5, 6, 7],  k = 3
Output: [5, 6, 7, 1, 2, 3, 4]
         \_______/ \_________/
         last 3     first 4
         wrap front  stay back
```

This document covers the **full journey** of your thinking — from the temp array idea, through two in-place approaches that TLE'd, to the optimal $O(n)$ / $O(1)$ reversal solution.

---

## 2. Input & Output

| Feature | Type | Description |
|---------|------|-------------|
| Input `nums` | `int[]` | Array of integers |
| Input `k` | `int` | Number of positions to rotate right |
| Output | `void` | Array mutated in-place |

### Examples

| Input | k | Output |
|-------|---|--------|
| `[1,2,3,4,5,6,7]` | 3 | `[5,6,7,1,2,3,4]` |
| `[1,2,3,4,5,6,7]` | 1 | `[7,1,2,3,4,5,6]` |
| `[1,2,3,4,5,6,7]` | 7 | `[1,2,3,4,5,6,7]` (full rotation = identity) |
| `[1,2,3,4,5,6,7]` | 10 | `[5,6,7,1,2,3,4]` (10 % 7 = 3) |
| `[-1,-100,3,99]` | 2 | `[3,99,-1,-100]` |

---

## 3. Core Concept — The `k % n` Insight

Before anything else, the most important observation:

**Rotating an array of size `n` by exactly `n` positions returns the original array.** Rotation is cyclic — after `n` steps you're back to the start.

```
[1,2,3]  rotate-right by 1 → [3,1,2]
[1,2,3]  rotate-right by 2 → [2,3,1]
[1,2,3]  rotate-right by 3 → [1,2,3]  ← same as original!
[1,2,3]  rotate-right by 4 → [3,1,2]  ← same as rotate by 1
```

So rotating by `k` is **identical** to rotating by `k % n`. This is why:

```java
k = k % n;
```

is the **first line** of any correct rotation solution. Without it, `k = 54944` on an array of size `n` does 54944 iterations when it only needs to do `54944 % n` — which could be tiny.

Your first approach had `k = k%n` but your second didn't — yet both TLE'd because the outer loop `k` iterations each doing an $O(n)$ inner loop gives $O(k \times n)$ which after the `%` reduction becomes $O((k\%n) \times n)$. For large `n` and `k % n` close to `n/2`, that's still $O(n^2)$.

---

## 4. Your Journey — Three Approaches

---

### 🚶 Approach 0 — New Array (Your Starting Point)

**The idea:** Compute where each element lands after rotation and write it directly into a fresh array.

After a right rotation by `k`, the element at index `i` moves to index `(i + k) % n`.

```java
public void rotate(int[] nums, int k) {
    int n = nums.length;
    k = k % n;
    int[] result = new int[n];

    for (int i = 0; i < n; i++) {
        result[(i + k) % n] = nums[i];
    }

    // Copy back into nums
    for (int i = 0; i < n; i++) {
        nums[i] = result[i];
    }
}
```

**Trace with `[1,2,3,4,5,6,7]`, k=3:**

```
i=0: result[(0+3)%7] = result[3] = 1
i=1: result[(1+3)%7] = result[4] = 2
i=2: result[(2+3)%7] = result[5] = 3
i=3: result[(3+3)%7] = result[6] = 4
i=4: result[(4+3)%7] = result[0] = 5
i=5: result[(5+3)%7] = result[1] = 6
i=6: result[(6+3)%7] = result[2] = 7

result = [5, 6, 7, 1, 2, 3, 4]  ✅
```

| Property | Value |
|----------|-------|
| **Time** | $O(n)$ |
| **Space** | $O(n)$ — extra array |
| **Passes** | 1 write + 1 copy = 2 |

This is clean and correct but uses $O(n)$ extra space. You then correctly asked: can we do it in-place? That led to Approach 1 and 2.

---

### 🚶 Approach 1 — Rotate Right by 1, K Times (Your Second Code)

```java
public void rotate(int[] nums, int k) {
    int n = nums.length;
    k = k % n;                        // ✅ correctly reduced

    for (int i = 1; i <= k; i++) {    // outer loop: k iterations
        int temp = nums[n - 1];       // save last element
        for (int j = n - 1; j > 0; j--) {
            nums[j] = nums[j - 1];    // shift right by 1
        }
        nums[0] = temp;               // place last at front
    }
}
```

**Mental Model:** Right-rotate by 1 means save the last element, shift everyone right, put the saved element at index 0. Do this `k` times.

**Trace — One single-rotation pass on `[1,2,3,4,5,6,7]`:**

```
Save: temp = 7
Shift right: [1,1,2,3,4,5,6]
Place: nums[0] = 7 → [7,1,2,3,4,5,6]
```

After 3 such passes: `[5,6,7,1,2,3,4]` ✅ Correct.

**Why it TLE'd:**

```
Outer loop: k iterations (after k%n reduction, still up to n-1)
Inner loop: n-1 iterations each time

Total: O(k × n)  →  worst case O(n²) when k ≈ n/2
```

For `n = 100,000` and `k = 50,000`: that's **5 billion operations**. TLE is guaranteed.

---

### 🚶 Approach 2 — Cyclic Carry-and-Place (Your First Code)

```java
public void rotate(int[] nums, int k) {
    int n = nums.length;

    for (int i = 1; i <= k; i++) {        // outer: k iterations ❌ no k%n!
        int temp = nums[0];
        for (int j = 0; j < n; j++) {
            int temp1 = temp;
            temp = nums[(j + 1) % n];
            nums[(j + 1) % n] = temp1;
        }
    }
}
```

**Mental Model:** Carry `nums[0]`, walk forward placing each carry into the next slot and picking up whatever was displaced.

**What does one pass do?**

```
arr = [1, 2, 3, 4, 5, 6, 7], temp=1

j=0: temp1=1, temp=nums[1]=2, nums[1]=1 → [1,1,3,4,5,6,7]
j=1: temp1=2, temp=nums[2]=3, nums[2]=2 → [1,1,2,4,5,6,7]
j=2: temp1=3, temp=nums[3]=4, nums[3]=3 → [1,1,2,3,5,6,7]
j=3: temp1=4, temp=nums[4]=5, nums[4]=4 → [1,1,2,3,4,6,7]
j=4: temp1=5, temp=nums[5]=6, nums[5]=5 → [1,1,2,3,4,5,7]
j=5: temp1=6, temp=nums[6]=7, nums[6]=6 → [1,1,2,3,4,5,6]
j=6: temp1=7, temp=nums[0]=1, nums[0]=7 → [7,1,2,3,4,5,6]
```

One pass = one **left rotation by 1** (not right!). After `k` such passes:
- k left rotations = n-k right rotations... only works when `k` is correctly interpreted.

**Why it TLE'd:**

```
No k%n reduction ❌ → k=54944 full iterations even if array is size 7
Outer: k iterations (unreduced!)
Inner: n iterations each

Total: O(k × n) — but k is the raw value, not k%n
For k=54944, n=large: catastrophically slow
```

Two bugs: missing `k = k % n`, and $O(k \times n)$ complexity even after the fix.

---

## 5. Why Both Approaches Have The Same Root Problem

Both approaches share the same fundamental structure:

```
repeat k times:
    do O(n) work
→ O(k × n) total
```

Even after `k = k % n`, this is $O(n^2)$ in the worst case. The only way to escape $O(n^2)$ is to **stop thinking about rotation as a repeated single-step operation** and instead find an approach that computes the final state directly in $O(n)$.

---

## 6. The Pattern — What Rotation Actually Does

Rotating `[1,2,3,4,5,6,7]` right by `k=3`:

```
Original:  [1, 2, 3, 4 | 5, 6, 7]
                        ^split at n-k

Result:    [5, 6, 7 | 1, 2, 3, 4]
            \_______/  \_________/
            last k      first n-k
            elements    elements
```

The last `k` elements become the new front. The first `n-k` elements become the new back. This is just a **rearrangement of two contiguous blocks**. Specifically:

```
Block A = nums[0   ... n-k-1]   (first n-k elements)
Block B = nums[n-k ... n-1  ]   (last k elements)

Original: [A | B]
Result:   [B | A]
```

Any $O(n)$ solution to this problem must exploit this block-swap structure directly.

---

## 7. Optimal Solution — Reversal Algorithm ⚡

### The Insight

Reversing is free ($O(1)$ space, $O(n)$ time). And three reversals can swap any two blocks:

```
[A | B]
→ reverse all    → [B_rev | A_rev]  → reverse each half
→ reverse B_rev  → [B | A_rev]
→ reverse A_rev  → [B | A]          ✅
```

More concretely for right rotation by `k`:

```
Step 1: Reverse the entire array
Step 2: Reverse the first k elements
Step 3: Reverse the remaining n-k elements
```

**Why does this work?**

```
Original:        [1, 2, 3, 4, 5, 6, 7]   (A=[1..4], B=[5..7])

Step 1 — rev all: [7, 6, 5, 4, 3, 2, 1]   (B reversed, A reversed, and swapped)

Step 2 — rev[0..k-1]=[0..2]:
                 [5, 6, 7, 4, 3, 2, 1]   (B_rev reversed back = B)

Step 3 — rev[k..n-1]=[3..6]:
                 [5, 6, 7, 1, 2, 3, 4]   (A_rev reversed back = A)  ✅
```

Three reversals. Each reversal is $O(n)$. Total = $O(n)$. No extra space.

---

### Code

```java
public void rotate(int[] nums, int k) {
    int n = nums.length;
    k = k % n;               // ✅ handle k > n and k = 0 edge cases

    if (k == 0) return;      // ✅ no rotation needed

    reverse(nums, 0, n - 1); // Step 1: reverse entire array
    reverse(nums, 0, k - 1); // Step 2: reverse first k elements
    reverse(nums, k, n - 1); // Step 3: reverse remaining n-k elements
}

private void reverse(int[] nums, int left, int right) {
    while (left < right) {
        int temp     = nums[left];
        nums[left]   = nums[right];
        nums[right]  = temp;
        left++;
        right--;
    }
}
```

---

### Full Trace

**Input:** `nums = [1,2,3,4,5,6,7]`, `k = 3`, `n = 7`

`k = 3 % 7 = 3`

**Step 1 — reverse(nums, 0, 6) — reverse entire array:**

```
[1, 2, 3, 4, 5, 6, 7]
 ^                 ^   swap 1↔7
[7, 2, 3, 4, 5, 6, 1]
    ^           ^      swap 2↔6
[7, 6, 3, 4, 5, 2, 1]
       ^     ^         swap 3↔5
[7, 6, 5, 4, 3, 2, 1]
          ^            middle (4) stays

Result: [7, 6, 5, 4, 3, 2, 1]
```

**Step 2 — reverse(nums, 0, 2) — reverse first k=3 elements:**

```
[7, 6, 5, 4, 3, 2, 1]
 ^     ^               swap 7↔5
[5, 6, 7, 4, 3, 2, 1]
    ^                  middle (6) stays

Result: [5, 6, 7, 4, 3, 2, 1]
```

**Step 3 — reverse(nums, 3, 6) — reverse last n-k=4 elements:**

```
[5, 6, 7, 4, 3, 2, 1]
          ^        ^   swap 4↔1
[5, 6, 7, 1, 3, 2, 4]
             ^  ^      swap 3↔2
[5, 6, 7, 1, 2, 3, 4]

Result: [5, 6, 7, 1, 2, 3, 4]  ✅
```

---

### Pseudocode

```plaintext
FUNCTION rotate(nums, k):
    SET n = length of nums
    SET k = k % n                   // reduce k, handle k > n

    IF k == 0 THEN RETURN           // no-op

    REVERSE nums from 0 to n-1      // flip entire array
    REVERSE nums from 0 to k-1      // flip first k (restores B in order)
    REVERSE nums from k to n-1      // flip last n-k (restores A in order)

END FUNCTION

FUNCTION reverse(nums, left, right):
    WHILE left < right:
        SWAP nums[left] AND nums[right]
        INCREMENT left
        DECREMENT right
    END WHILE
END FUNCTION
```

---

### Complexity

| Property | Value |
|----------|-------|
| **Time Complexity** | $O(n)$ — each element is touched at most twice across all 3 reversals |
| **Space Complexity** | $O(1)$ — only `temp`, `left`, `right` pointers |
| **Mutates Input?** | ✅ Yes |
| **Handles k > n?** | ✅ Yes — `k = k % n` |
| **Handles k = 0?** | ✅ Yes — early return |
| **Handles n = 1?** | ✅ Yes — `k % 1 = 0`, early return |

---

## 8. All Approaches — Full Comparison

| | Temp Array | Rotate-by-1 k times | Carry-and-Place k times | **Reversal** |
|--|-----------|---------------------|------------------------|-------------|
| **Time** | $O(n)$ | $O(k \times n)$ → $O(n^2)$ | $O(k \times n)$ → $O(n^2)$ | **$O(n)$** |
| **Space** | $O(n)$ | $O(1)$ | $O(1)$ | **$O(1)$** |
| **Passes** | 2 | $k \times n$ steps | $k \times n$ steps | $3 \times \frac{n}{2}$ steps |
| **k%n needed?** | ✅ | ✅ | ❌ missing in your code | ✅ |
| **Passes 39/40?** | ✅ | ✅ | ✅ | ✅ All 40 |
| **TLE on large k?** | ❌ | ✅ TLE | ✅ TLE | ❌ Never |
| **Correct?** | ✅ | ✅ | ✅ | ✅ |

---

## 9. The TLE Test Case — Why `k=54944` Broke You

Given your description: `nums` has a very large length (let's call it `N`) and `k = 54944`.

**Approach 1 (rotate-by-1 k times):**
After `k = k % n`, suppose `k % N = 30000`.
```
30000 outer iterations × N inner iterations = 30000N operations
If N = 100,000: 3,000,000,000 operations  → TLE ✅
```

**Approach 2 (carry-and-place, no k%n):**
`k = 54944` (unreduced).
```
54944 outer iterations × N inner iterations = 54944N operations
If N = 100,000: 5,494,400,000 operations  → TLE ✅
```

**Reversal approach:**
```
3 × N/2 swap operations = 1.5N operations
If N = 100,000: 150,000 operations  → instant ✅
```

The gap is not a small constant — it's the difference between **millions/billions of operations** and **150,000 operations**. This is why Big-O complexity is not academic — it is the difference between passing and failing at scale.

---

## 10. Common Mistakes & Pitfalls

| Mistake | Why it's wrong | Fix |
|--------|----------------|-----|
| Missing `k = k % n` | k=54944 on small array runs 54944 full passes | Always reduce k first |
| Using `k = 0` check without `k % n` first | `k = n` passes the `k == 0` check but shouldn't rotate | Do `k = k % n` before the check |
| `reverse(nums, 0, k)` instead of `0, k-1` | Off-by-one — reverses k+1 elements instead of k | Use `k-1` as right bound |
| `reverse(nums, k-1, n-1)` instead of `k, n-1` | Off-by-one — skips or re-reverses element at k-1 | Use `k` as left bound |
| Reversing in wrong order (step 2 before step 1) | Steps 2 and 3 depend on step 1 having happened first | Always: full → first-k → last-(n-k) |
| Left rotation instead of right | Left by k = right by n-k, easy to mix up | Right rotation: `reverse all → reverse [0,k-1] → reverse [k,n-1]` |