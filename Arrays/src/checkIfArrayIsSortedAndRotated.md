# 🔄 Algorithm: Check if Array is Sorted and Rotated

---

## 1. Description

`check` determines whether an integer array `nums` is a **sorted array that has been rotated** some number of positions (including zero rotations — the original sorted order). It returns `true` if the array could be a rotation of a non-decreasing sorted array, and `false` otherwise.

The entire solution fits in a **single linear pass** and relies on one elegant insight: counting how many times a "drop" occurs in the array when it is viewed as a **circular sequence**.

---

## 2. Input & Output

| Feature | Type | Description |
|---------|------|-------------|
| Input | `int[] nums` | Array of integers (may contain duplicates) |
| Output | `boolean` | `true` if sorted+rotated, `false` otherwise |

### Output Cases

| Scenario | Input | Output | Why |
|----------|-------|--------|-----|
| Already sorted | `[1, 2, 3, 4, 5]` | `true` | 0 drops — valid (0 rotations) |
| Rotated sorted | `[3, 4, 5, 1, 2]` | `true` | 1 drop (5→1) |
| Rotated with dups | `[2, 1, 3, 4]` | `false` | 2 drops (2→1 and 4→2 wrap) |
| Unsorted | `[1, 3, 2, 4, 5]` | `false` | 2 drops |
| All same | `[1, 1, 1, 1]` | `true` | 0 drops |
| Single element | `[5]` | `true` | 0 drops |
| Two elements | `[2, 1]` | `true` | 1 drop (2→1), wraps back 1→2 ✅ |

---

## 3. The Key Insight — What is a "Drop"?

A **drop** is a position `i` where:

```
nums[i] > nums[i+1]
```

meaning the value **decreases** going from index `i` to the next index. In a non-decreasing sorted array, values never decrease — there are **zero drops**.

When you **rotate** a sorted array, you cut it at one point and move the tail to the front:

```
Original sorted: [1, 2, 3, 4, 5]

Rotate by 2:     [4, 5, 1, 2, 3]
                        ^
                    One drop here (5 → 1)
```

Rotating creates **exactly one drop** — the boundary where the high tail meets the low head.

The wrap-around position (`nums[n-1]` back to `nums[0]`) also needs checking. In a sorted+rotated array, the last element reconnects smoothly to the first (since the array is circular). This wrap-around counts as the **one allowed drop** in the already-sorted case, except when the array is already sorted in which case `nums[n-1] <= nums[0]` wraps cleanly with 0 drops.

**The Rule:**

```
drops == 0  →  array is sorted (zero rotations)  →  true
drops == 1  →  exactly one rotation point exists  →  true
drops >= 2  →  array is neither sorted nor a clean rotation  →  false
```

---

## 4. Thinking & Mental Model

### 🧠 The "Circular Necklace" Model

Instead of thinking of the array as a flat line, think of it as a **circular necklace** of beads, each bead labeled with a number.

```
[3, 4, 5, 1, 2]  visualized as a circle:

        3
      /   \
    2       4
    |       |
    1       5
      \   /
        ↑
     one drop here (5 → 1)
```

In a valid sorted-and-rotated array, as you walk around the necklace clockwise, the values go **up, up, up... then ONE step down**, and then continue going up again until you complete the circle. The single step-down is the rotation point.

If you ever see **two or more step-downs**, the necklace is scrambled — not a clean rotation of a sorted sequence.

---

### 🔍 Why `(i+1) % n`?

The modulo operator `%` wraps the index back to `0` when `i` reaches `n-1`:

```
When i = n-1:
    (i + 1) % n = n % n = 0

So nums[n-1] is compared to nums[0]
```

This is what makes the check **circular** — it connects the last element back to the first, completing the necklace. Without this, you'd miss the wrap-around drop (or non-drop) and get wrong answers for rotated arrays.

```
Array: [3, 4, 5, 1, 2]  (n=5)

i=0: nums[0]=3 vs nums[1]=4  → 3 > 4? No
i=1: nums[1]=4 vs nums[2]=5  → 4 > 5? No
i=2: nums[2]=5 vs nums[3]=1  → 5 > 1? YES → cnt=1
i=3: nums[3]=1 vs nums[4]=2  → 1 > 2? No
i=4: nums[4]=2 vs nums[0]=3  → 2 > 3? No  ← wrap-around (i+1)%n = 0

cnt = 1 → return true ✅
```

---

## 5. Step-by-Step Code Breakdown

### Step 1 — Setup

```java
int n   = nums.length;
int cnt = 0;
```

`n` stores the array length. `cnt` is the drop counter, initialized to `0`.

---

### Step 2 — The Circular Loop

```java
for (int i = 0; i < n; i++) {
    if (nums[i] > nums[(i + 1) % n]) cnt++;
}
```

Iterates over **every index** including the last one (`i = n-1`). For each position, compares `nums[i]` with its circular next neighbor `nums[(i+1) % n]`. If the current value is strictly greater than the next — a drop — increment `cnt`.

The loop runs exactly `n` times, checking all `n` adjacent pairs in the circular arrangement:

```
Pairs checked: (0,1), (1,2), (2,3), ..., (n-2, n-1), (n-1, 0)
                                                        ^^^^^^^^
                                                    wrap-around pair
```

---

### Step 3 — The Return

```java
if (cnt <= 1) return true;
else return false;
```

The commented-out block above this is the **expanded version** of the same logic. Both are identical — the short form just removes the redundant structure.

```java
// Expanded (commented out) — exactly equivalent:
if (cnt <= 1) {
    return true;
} else {
    return false;
}

// Short form — cleaner, same result:
if (cnt <= 1) return true;
else return false;

// Even shorter — returns the boolean expression directly:
return cnt <= 1;
```

All three produce the same output. The cleanest form is `return cnt <= 1`.

---

## 6. Full Traces

### Trace 1 — Rotated Sorted Array ✅

**Input:** `nums = [4, 5, 6, 1, 2, 3]`

| i | nums[i] | nums[(i+1)%n] | Drop? | cnt |
|---|---------|--------------|-------|-----|
| 0 | 4 | 5 | 4 > 5? No | 0 |
| 1 | 5 | 6 | 5 > 6? No | 0 |
| 2 | 6 | 1 | 6 > 1? **YES** | 1 |
| 3 | 1 | 2 | 1 > 2? No | 1 |
| 4 | 2 | 3 | 2 > 3? No | 1 |
| 5 | 3 | 4 | 3 > 4? No | 1 |

`cnt = 1 ≤ 1` → **`true`** ✅

---

### Trace 2 — Unsorted Array ❌

**Input:** `nums = [1, 3, 2, 4, 5]`

| i | nums[i] | nums[(i+1)%n] | Drop? | cnt |
|---|---------|--------------|-------|-----|
| 0 | 1 | 3 | 1 > 3? No | 0 |
| 1 | 3 | 2 | 3 > 2? **YES** | 1 |
| 2 | 2 | 4 | 2 > 4? No | 1 |
| 3 | 4 | 5 | 4 > 5? No | 1 |
| 4 | 5 | 1 | 5 > 1? **YES** | 2 |

`cnt = 2 > 1` → **`false`** ❌

---

### Trace 3 — Already Sorted (Zero Rotations) ✅

**Input:** `nums = [1, 2, 3, 4, 5]`

| i | nums[i] | nums[(i+1)%n] | Drop? | cnt |
|---|---------|--------------|-------|-----|
| 0 | 1 | 2 | No | 0 |
| 1 | 2 | 3 | No | 0 |
| 2 | 3 | 4 | No | 0 |
| 3 | 4 | 5 | No | 0 |
| 4 | 5 | 1 | 5 > 1? **YES** | 1 |

`cnt = 1 ≤ 1` → **`true`** ✅

> The wrap-around comparison `nums[4]=5 vs nums[0]=1` creates one drop. This is the "rotation point" of a zero-rotation array — where the end connects back to the beginning. It counts as the one allowed drop.

---

### Trace 4 — All Identical ✅

**Input:** `nums = [2, 2, 2, 2]`

No comparison ever satisfies `nums[i] > nums[(i+1)%n]` since all values are equal (not strictly greater). `cnt = 0 ≤ 1` → **`true`** ✅

---

### Trace 5 — Two Elements ✅

**Input:** `nums = [2, 1]`

| i | nums[i] | nums[(i+1)%n] | Drop? | cnt |
|---|---------|--------------|-------|-----|
| 0 | 2 | 1 | 2 > 1? **YES** | 1 |
| 1 | 1 | 2 | 1 > 2? No | 1 |

`cnt = 1 ≤ 1` → **`true`** ✅ (`[2, 1]` is `[1, 2]` rotated by 1)

---

## 7. Algorithmic Approach

| Property | Value |
|----------|-------|
| **Strategy** | Circular drop counting |
| **Time Complexity** | $O(n)$ — single pass, exactly `n` comparisons |
| **Space Complexity** | $O(1)$ — only `cnt`, `n`, `i` |
| **Mutates Input?** | ❌ No |
| **Handles Duplicates?** | ✅ Yes — uses strict `>`, so equal neighbors don't count as drops |
| **Handles Single Element?** | ✅ Yes — loop runs once, `(0+1)%1 = 0`, compares `nums[0]` to itself, no drop |

---

## 8. Pseudocode

```plaintext
FUNCTION check(nums):
    SET n   = length of nums
    SET cnt = 0                             // drop counter

    FOR i FROM 0 TO n-1:
        IF nums[i] > nums[(i+1) % n] THEN  // circular next neighbor
            INCREMENT cnt
        END IF
    END FOR

    IF cnt <= 1 THEN
        RETURN true                         // sorted or cleanly rotated
    ELSE
        RETURN false                        // two or more drops = scrambled
    END IF

END FUNCTION
```

---

## 9. Optimised Code — Cleaner Expression

The logic is already $O(n)$ and $O(1)$ — it cannot be improved in complexity. The only improvement is **code clarity**:

```java
public boolean check(int[] nums) {
    int n   = nums.length;
    int cnt = 0;

    for (int i = 0; i < n; i++) {
        if (nums[i] > nums[(i + 1) % n]) cnt++;
    }

    return cnt <= 1;   // ✅ eliminates the if/else entirely
}
```

`return cnt <= 1` directly returns the boolean result of the comparison — no `if/else` needed. The expression `cnt <= 1` is already a `boolean` value (`true` or `false`), so wrapping it in an `if/else` just to return `true` or `false` is redundant.

This pattern applies broadly in Java:

```java
// ❌ Verbose and redundant:
if (condition) return true;
else return false;

// ✅ Clean and direct:
return condition;
```

---

## 10. Why This Works — The Math Behind It

For any array of length `n`, there are exactly `n` circular adjacent pairs. In a non-decreasing sorted array viewed circularly:

- All `n` pairs satisfy `nums[i] <= nums[i+1]` — **zero drops**
- Except potentially the wrap-around `(n-1, 0)` where the largest value reconnects to the smallest — **at most one drop**

When you rotate the array by `k` positions, the drop that was at the wrap-around `(n-1, 0)` moves to position `(n-k-1, n-k)` — but **it's still just one drop**. The number of drops is invariant to rotation for sorted arrays.

Any array with **two or more drops** cannot be "unrotated" back to a non-decreasing sequence, because a single rotation can only move one drop — it can never merge two drops into zero.

```
Visualization:

Sorted:          [1, 2, 3, 4, 5] — drops: (5→1 wrap) = 1
Rotate by 1:     [5, 1, 2, 3, 4] — drops: (5→1) = 1   ✅
Rotate by 2:     [4, 5, 1, 2, 3] — drops: (5→1) = 1   ✅
Unsorted:        [1, 3, 2, 4, 5] — drops: (3→2) + (5→1 wrap) = 2  ❌
```

---

## 11. Common Mistakes & Pitfalls

| Mistake | Why it's wrong | Fix |
|--------|----------------|-----|
| Using `i < n-1` instead of `i < n` | Misses the wrap-around pair `(n-1, 0)` — fails for rotated arrays | Use `i < n` with `(i+1) % n` |
| Using `nums[i+1]` instead of `nums[(i+1)%n]` | `ArrayIndexOutOfBoundsException` at `i = n-1` | Always use modulo for circular access |
| Using `>=` instead of `>` | Equal neighbors counted as drops — breaks for duplicates | Use strict `>` only |
| Checking `cnt == 1` instead of `cnt <= 1` | Rejects already-sorted arrays which have `cnt = 0` | Use `cnt <= 1` |
| `if (x) return true; else return false;` | Redundant — `x` is already a boolean | Use `return x` directly |