# 👯 Algorithm: Remove Duplicates from Sorted Array (In-Place)

---

## 1. Description

`removeDuplicates` removes duplicate values from a **sorted** integer array **in-place** and returns the count of unique elements. It does NOT create a new array — it overwrites the front portion of the existing array with the unique values, then returns how many unique values exist.

The approach uses the **Two Pointer (Slow-Fast)** technique: one pointer tracks where the next unique value should be written, the other scans ahead looking for new unique values.

---

## 2. Input & Output

| Feature | Type | Description |
|---------|------|-------------|
| Input | `int[] nums` | A **sorted** (non-decreasing) array of integers |
| Output | `int` | Count `k` of unique elements |

### The In-Place Contract

The caller is expected to look at only the first `k` elements of `nums` after the function returns. Elements beyond index `k-1` are irrelevant — they may contain old values or garbage.

```
Input:  nums = [1, 1, 2, 3, 3, 4]
Output: k = 4
nums after call: [1, 2, 3, 4, _, _]
                  ^^^^^^^^^^^  ^^^^
                  first k=4    don't care
                  elements
                  are unique
```

---

## 3. Why "Sorted" is a Prerequisite

This algorithm **only works because the array is sorted**. In a sorted array, all duplicates of a value are grouped together — they are always adjacent.

```
Sorted:   [1, 1, 2, 3, 3, 3, 4]
               ^^       ^^^
           duplicates adjacent — easy to detect by comparing neighbors

Unsorted: [1, 3, 1, 2, 3, 4, 3]
           duplicates scattered — comparing neighbors doesn't work
```

Because duplicates are adjacent in a sorted array, you only ever need to compare an element with its **immediate predecessor** to know if it's a duplicate. No lookups, no hash sets, no sorting step needed.

---

## 4. Core Concept — The Two Pointer (Slow-Fast) Pattern

This is one of the most fundamental patterns in array problems. Two pointers move through the array at different speeds or for different purposes:

```
slow  →  the "write head" — points to the last confirmed unique element
fast  →  the "read head"  — scans forward looking for new unique elements
```

Think of it as a **filter and copy machine**:

- `fast` reads every element.
- When `fast` finds a value different from what `slow` is sitting on, it's a new unique value — write it to `slow+1` and advance `slow`.
- When `fast` finds a duplicate (same value as `slow`), skip it — just advance `fast`.

At the end, `slow` is the index of the last unique element. The count of unique elements is `slow + 1`.

---

## 5. Thinking & Mental Model

### 🧠 The "Ink Stamp" Model

Imagine `slow` as an ink stamp that only moves forward when it finds something genuinely new to stamp. `fast` is your eye scanning along the page.

```
nums = [1, 1, 2, 3, 3, 4]

slow=0 (stamped 1 at position 0)
fast scans...
  fast=1: sees 1 — same as stamp → skip
  fast=2: sees 2 — NEW! → advance slow to 1, stamp 2 there
  fast=3: sees 3 — NEW! → advance slow to 2, stamp 3 there
  fast=4: sees 3 — same as stamp → skip
  fast=5: sees 4 — NEW! → advance slow to 3, stamp 4 there

Final stamp position: slow=3
Unique count: 3+1 = 4
```

### 🧠 The "Building a Clean Prefix" Model

Think of the array as having two zones at all times:

```
[ ✅ clean unique prefix | 🔍 being scanned | ❓ not yet visited ]
   index 0 ... slow         slow+1...fast-1     fast ... n-1
```

- The left zone `[0...slow]` is always sorted and unique — the "done" region.
- `fast` explores the right zone and recruits new unique values into the left zone.
- The left zone grows by one each time `fast` finds a new value.

---

## 6. Step-by-Step Code Breakdown

### Step 1 — Initialize `slow`

```java
int slow = 0;
```

`slow` starts at index `0`. The first element is always unique (there's nothing before it to compare against), so `slow` starts "already having accepted" `nums[0]` as the first unique element.

---

### Step 2 — `fast` Scans from Index 1

```java
for (int fast = 1; fast < nums.length; fast++) {
```

`fast` starts at `1` because we're comparing `nums[fast]` against `nums[slow]`. Starting at `0` would compare `nums[0]` against itself — always equal, always a false duplicate.

`fast` runs to `nums.length - 1` covering every remaining element.

---

### Step 3 — The Core Decision

```java
if (nums[fast] != nums[slow]) {
    slow++;
    nums[slow] = nums[fast];
}
```

Two things happen **only when a new unique value is found:**

**`slow++`** — Advance the write head to the next available slot.

**`nums[slow] = nums[fast]`** — Write the new unique value into that slot.

When `nums[fast] == nums[slow]`, nothing happens. `fast` will advance on the next loop iteration naturally. The duplicate is silently ignored.

**Order matters:** `slow++` happens **before** `nums[slow] = nums[fast]`. If you wrote first then incremented, you'd overwrite the current unique value with a duplicate before moving on. The increment must come first to move to the **next free slot**.

---

### Step 4 — Return `slow + 1`

```java
return slow + 1;
```

`slow` is a **0-based index** of the last unique element. The **count** of unique elements is always index + 1.

```
slow = 0 → 1 unique element
slow = 1 → 2 unique elements
slow = 3 → 4 unique elements
```

---

## 7. Full Trace

**Input:** `nums = [0, 0, 1, 1, 1, 2, 2, 3, 3, 4]`

| fast | nums[fast] | nums[slow] | Duplicate? | Action | slow | Array state |
|------|-----------|-----------|------------|--------|------|-------------|
| Start | — | nums[0]=0 | — | — | 0 | `[0, 0, 1, 1, 1, 2, 2, 3, 3, 4]` |
| 1 | 0 | 0 | ✅ Yes | skip | 0 | `[0, 0, 1, 1, 1, 2, 2, 3, 3, 4]` |
| 2 | 1 | 0 | ❌ New | slow→1, write 1 | 1 | `[0, 1, 1, 1, 1, 2, 2, 3, 3, 4]` |
| 3 | 1 | 1 | ✅ Yes | skip | 1 | `[0, 1, 1, 1, 1, 2, 2, 3, 3, 4]` |
| 4 | 1 | 1 | ✅ Yes | skip | 1 | `[0, 1, 1, 1, 1, 2, 2, 3, 3, 4]` |
| 5 | 2 | 1 | ❌ New | slow→2, write 2 | 2 | `[0, 1, 2, 1, 1, 2, 2, 3, 3, 4]` |
| 6 | 2 | 2 | ✅ Yes | skip | 2 | `[0, 1, 2, 1, 1, 2, 2, 3, 3, 4]` |
| 7 | 3 | 2 | ❌ New | slow→3, write 3 | 3 | `[0, 1, 2, 3, 1, 2, 2, 3, 3, 4]` |
| 8 | 3 | 3 | ✅ Yes | skip | 3 | `[0, 1, 2, 3, 1, 2, 2, 3, 3, 4]` |
| 9 | 4 | 3 | ❌ New | slow→4, write 4 | 4 | `[0, 1, 2, 3, 4, 2, 2, 3, 3, 4]` |

**Return:** `slow + 1 = 5`

**Result:** First 5 elements of `nums` = `[0, 1, 2, 3, 4]` ✅

Notice the values beyond index 4 (`2, 2, 3, 3, 4`) — they are leftover from the original array but are irrelevant since the caller only reads the first `k=5` elements.

---

## 8. The "Slow Pointer Never Overtakes Fast" Guarantee

A subtle but important property: `slow` can **never be ahead of or equal to `fast`** (except at the very start where both positions are essentially `0` and `1`).

Why? `slow` only increments when `fast` finds a new unique value — meaning `fast` must have already moved past `slow`. `slow` chases `fast` but always stays behind it. This guarantees:

```
nums[slow] = nums[fast]
```

never overwrites a value that `fast` hasn't visited yet. The write is always safe.

In the worst case (all elements unique), `slow` and `fast` move together in lockstep, each incrementing every iteration. In the best case (all elements identical), `slow` stays at `0` while `fast` runs all the way to `n-1`.

---

## 9. Algorithmic Approach

| Property | Value |
|----------|-------|
| **Pattern** | Two Pointer — Slow/Fast (Read/Write) |
| **Time Complexity** | $O(n)$ — single pass, `fast` visits each element exactly once |
| **Space Complexity** | $O(1)$ — no extra array, only two pointer variables |
| **Mutates Input?** | ✅ Yes — overwrites the front portion of `nums` |
| **Requires Sorted Input?** | ✅ Yes — duplicates must be adjacent |
| **Stable?** | ✅ Yes — relative order of unique elements is preserved |

---

## 10. Pseudocode

```plaintext
FUNCTION removeDuplicates(nums):
    SET slow = 0                            // write head: last unique index

    FOR fast FROM 1 TO length(nums) - 1:   // read head: scans every element
        IF nums[fast] != nums[slow] THEN    // found a new unique value
            INCREMENT slow                  // advance write head to next slot
            SET nums[slow] = nums[fast]     // write unique value into slot
        END IF
        // else: duplicate — fast advances automatically, slow stays
    END FOR

    RETURN slow + 1                         // index → count conversion

END FUNCTION
```

---

## 11. Optimised Approach — Is There Anything Better?

**Time:** Already $O(n)$ — you must read every element at least once to know if it's a duplicate. Cannot do better than $O(n)$.

**Space:** Already $O(1)$ — no auxiliary data structures.

**The algorithm is optimal as-is.** The only meaningful "optimisation" is an **early exit** for edge cases:

```java
public int removeDuplicates(int[] nums) {
    if (nums.length == 0) return 0;   // ✅ guard against empty array

    int slow = 0;

    for (int fast = 1; fast < nums.length; fast++) {
        if (nums[fast] != nums[slow]) {
            slow++;
            nums[slow] = nums[fast];
        }
    }

    return slow + 1;
}
```

The original code has a subtle bug: if `nums` is empty (`length = 0`), `fast` starts at `1` but `nums.length = 0`, so the loop never runs — and `slow + 1 = 1` is returned, incorrectly claiming there is 1 unique element in an empty array. The empty array guard fixes this.

### Can We Skip the Write When `slow` and `fast` Are Adjacent?

When all elements are unique, `slow` and `fast` always differ by exactly 1, so every iteration does:
```java
slow++;
nums[slow] = nums[fast];    // writing nums[fast] to nums[fast-1+1] = nums[fast]
                            // writing a value to itself — no-op
```

You could skip the write with:

```java
if (nums[fast] != nums[slow]) {
    slow++;
    if (slow != fast) nums[slow] = nums[fast];   // skip self-assignment
}
```

But this adds a branch on every iteration to avoid a self-assignment that is already harmless. In practice this is **not worth it** — branch misprediction overhead can exceed the cost of a simple array write. Leave it as-is.

---

## 12. The Two Pointer Pattern — Broader Context

The slow/fast pointer pattern used here appears across many array problems. Recognizing the shape helps you apply it quickly:

| Problem | Slow Pointer Role | Fast Pointer Role |
|---------|------------------|------------------|
| **Remove Duplicates (this)** | Write head for unique values | Read head scanning for new values |
| Remove Element (remove all `val`) | Write head for kept values | Read head scanning all elements |
| Move Zeroes to end | Write head for non-zeros | Read head finding non-zeros |
| Partition array (Dutch Flag) | Boundary of left partition | Current element being placed |
| Linked List cycle detection | Moves 1 step at a time | Moves 2 steps at a time |

**The general shape:**

```
slow = starting position
for fast in range(array):
    if fast's element meets some condition:
        use fast's element to update slow's position
        advance slow
```

Whenever you see "remove/keep certain elements in-place" or "find a boundary in an array," the slow-fast two pointer is almost always the right tool.

---

## 13. Common Mistakes & Pitfalls

| Mistake | Why it's wrong | Fix |
|--------|----------------|-----|
| `fast` starts at `0` | Compares `nums[0]` to itself — first element always skipped | Start `fast` at `1` |
| `nums[slow] = nums[fast]` before `slow++` | Overwrites current unique value with duplicate before moving on | Always `slow++` first, then write |
| Returning `slow` instead of `slow + 1` | `slow` is the last index, not the count | Return `slow + 1` |
| Using on unsorted array | Duplicates are non-adjacent — algorithm misses them | Sort first, or use a HashSet |
| No empty array guard | Returns `1` for empty input instead of `0` | Add `if (nums.length == 0) return 0` |
| Using `slow < fast` as loop condition | Wrong — `fast` drives the loop, `slow` is passive | Use `fast < nums.length` as the loop bound |