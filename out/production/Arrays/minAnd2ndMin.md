# 🔍 Algorithm: Find Minimum and Second Minimum (Distinct)

---

## 1. Description

`minAnd2ndMin` finds the **two smallest distinct values** in an integer array. It returns them in a list as `[minimum, second_minimum]`. If no second distinct minimum exists (all elements are identical), it returns `[-1]`.

This document covers **two complete approaches**:

| | Approach 1 | Approach 2 |
|--|-----------|-----------|
| **Strategy** | Sort then Scan | Single Pass |
| **Time** | $O(n^2)$ or $O(n \log n)$ | $O(n)$ |
| **Mutates input?** | ✅ Yes | ❌ No |
| **Difficulty** | Beginner | Intermediate |

---

## 2. Input & Output

| Feature | Type | Description |
|---------|------|-------------|
| Input | `int[] arr` | A non-empty array of integers. |
| Output | `ArrayList<Integer>` | `[min, secondMin]` if two distinct values exist, or `[-1]` if they don't. |

### Output Cases

| Scenario | Example Input | Output |
|----------|--------------|--------|
| Normal case | `[4, 2, 7, 1, 3]` | `[1, 2]` |
| Duplicates of min exist | `[3, 1, 1, 2, 5]` | `[1, 2]` (skips duplicate 1s) |
| All elements identical | `[5, 5, 5, 5]` | `[-1]` |
| Two elements, distinct | `[9, 3]` | `[3, 9]` |
| Two elements, identical | `[4, 4]` | `[-1]` |

---

## 3. Core Concept

The problem is NOT just "find the two smallest elements" — it is "find the two smallest **distinct** values."

This distinction matters critically when duplicates exist:

```
arr = [1, 1, 1, 2, 3]

Smallest two elements by position: 1 and 1  ❌  (same value, not distinct)
Smallest two DISTINCT values:      1 and 2  ✅
```

Both approaches must handle this. They just do it differently.

---

## 4. Thinking & Mental Model

### 🧠 The "Front of a Sorted Line" Model (Approach 1)

Imagine sorting people by height, shortest to tallest. Several people may share the same shortest height — they all stand at the front. The **second minimum height** is the height of the first person who is *taller* than that front group.

```
Sorted: [1, 1, 1, 2, 3, 4]
         \_____/  ^
         min=1    secondMin=2  (first value ≠ min)
```

You don't care about positions — only about **distinct height values**.

### 🧠 The "Two Champions" Model (Approach 2)

Imagine a live competition where you track two titles at once: **Champion** (overall minimum) and **Runner-up** (second minimum). As each new contender arrives:

- If the new contender beats the **Champion**, the old Champion gets demoted to Runner-up, and the new contender becomes Champion.
- If the new contender doesn't beat the Champion but beats the **Runner-up** (and isn't the same value as Champion), they become the new Runner-up.
- Otherwise, the contender is irrelevant.

```
Stream: [3, 1, 4, 1, 5, 2]

x=3 → Champion=3,  Runner-up=∞
x=1 → Champion=1,  Runner-up=3   (1 beats 3, 3 demoted)
x=4 → no change    (4 > Runner-up 3)
x=1 → no change    (1 == Champion, not distinct)
x=5 → no change    (5 > Runner-up 3)
x=2 → Runner-up=2  (2 < Runner-up 3, and 2 ≠ Champion 1)

Final: [1, 2] ✅
```

---

## 5. Why Not Just Use `arr[0]` and `arr[1]`?

A common first instinct after sorting: just return the first two elements. This fails when duplicates of the minimum exist:

```
arr = [1, 1, 1, 2, 3]  (after sorting)

arr[0] = 1  ✅ minimum
arr[1] = 1  ❌ NOT the second distinct minimum — same value!
```

Both approaches must explicitly skip over duplicates of the minimum to find the first genuinely different value.

---

## 6. Approach 1 — Sort Then Scan

### Strategy
Sort the array so the smallest values cluster at index 0, then scan left-to-right for the first element that differs from `arr[0]`.

### Code

```java
public ArrayList<Integer> minAnd2ndMin(int[] arr) {
    int n = arr.length;

    // Phase 1: Sort in ascending order using Insertion Sort
    for (int i = 0; i < n - 1; i++) {
        for (int j = i + 1; j > 0; j--) {
            if (arr[j] < arr[j - 1]) {
                int temp   = arr[j];
                arr[j]     = arr[j - 1];
                arr[j - 1] = temp;
            }
        }
    }

    // Phase 2: Initialize
    ArrayList<Integer> small = new ArrayList<>();
    int firstElement  = arr[0];
    int secondElement = Integer.MIN_VALUE;   // Sentinel: "not found yet"

    // Phase 3: Scan for first element different from minimum
    for (int i : arr) {
        if (i != firstElement) {
            secondElement = i;
            break;
        }
    }

    // Phase 4: No second distinct element found
    if (secondElement == Integer.MIN_VALUE) {
        small.add(-1);
        return small;
    }

    // Phase 5: Return both
    small.add(firstElement);
    small.add(secondElement);
    return small;
}
```

---

### Phase-by-Phase Breakdown

---

#### Phase 1 — Sort the Array (Insertion Sort)

```java
for (int i = 0; i < n - 1; i++) {
    for (int j = i + 1; j > 0; j--) {
        if (arr[j] < arr[j - 1]) {
            int temp   = arr[j];
            arr[j]     = arr[j - 1];
            arr[j - 1] = temp;
        }
    }
}
```

Sorts the array in ascending order. After this, the minimum is guaranteed to be at index 0.

The commented line `// Arrays.sort(arr)` reveals intent — sorting is the goal and `Arrays.sort()` would do the same job faster at $O(n \log n)$. The Insertion Sort here is a manual implementation, likely for practice.

> **Note:** This mutates the original array permanently. The caller's array is changed.

```
Input:  [4, 2, 7, 1, 3]
Output: [1, 2, 3, 4, 7]   ← minimum is guaranteed at index 0
```

---

#### Phase 2 — Initialize the Two Candidates

```java
int firstElement  = arr[0];
int secondElement = Integer.MIN_VALUE;
```

**`firstElement = arr[0]`** — After sorting, `arr[0]` is the minimum. No scanning needed.

**`secondElement = Integer.MIN_VALUE`** — This is Java's smallest possible integer: `-2,147,483,648`. It is used as a **sentinel value** — a placeholder meaning "not yet found." It is checked in Phase 4 to detect the all-identical case.

> ⚠️ **Sentinel Risk:** If the array actually contains `Integer.MIN_VALUE`, the sentinel check fires incorrectly and returns `[-1]` even when a second minimum exists. See Section 8 for the fix.

---

#### Phase 3 — Find the Second Distinct Minimum

```java
for (int i : arr) {
    if (i != firstElement) {
        secondElement = i;
        break;
    }
}
```

Scans from left to right. Since the array is sorted, all occurrences of the minimum are clustered at the front. The **first element that differs** from `firstElement` is the second minimum. We `break` immediately — no need to scan further.

**Trace with `[1, 1, 1, 2, 3]`:**
```
i=1:  1 == firstElement(1)  → skip
i=1:  1 == firstElement(1)  → skip
i=1:  1 == firstElement(1)  → skip
i=2:  2 != firstElement(1)  → secondElement = 2, BREAK ✅
```

**Trace with `[5, 5, 5, 5]`:**
```
i=5, i=5, i=5, i=5  → all equal firstElement → loop ends
secondElement still = Integer.MIN_VALUE
```

---

#### Phase 4 — Handle "No Second Minimum"

```java
if (secondElement == Integer.MIN_VALUE) {
    small.add(-1);
    return small;
}
```

`secondElement` was never updated → all elements are identical → no second distinct minimum exists → return `[-1]`.

**Why `-1`?** Convention on competitive platforms (LeetCode, GeeksForGeeks) for "not found." The caller is expected to check for it.

---

#### Phase 5 — Return

```java
small.add(firstElement);
small.add(secondElement);
return small;
```

Both values confirmed valid. Return `[minimum, secondMinimum]`.

---

### Approach 1: Complexity

| Property | Value |
|----------|-------|
| **Time Complexity** | $O(n^2)$ (Insertion Sort) — $O(n \log n)$ with `Arrays.sort()` |
| **Space Complexity** | $O(1)$ extra |
| **Mutates Input?** | ✅ Yes |
| **Handles Duplicates?** | ✅ Yes |
| **Handles All-Identical?** | ✅ Yes |

---

### Approach 1: Full End-to-End Trace

**Input:** `arr = [3, 1, 4, 1, 5, 2]`

```
Phase 1 — Sort:
[3, 1, 4, 1, 5, 2]  →  [1, 1, 2, 3, 4, 5]

Phase 2 — Initialize:
firstElement  = 1
secondElement = Integer.MIN_VALUE

Phase 3 — Scan:
i=1: 1 == 1 → skip
i=1: 1 == 1 → skip
i=2: 2 != 1 → secondElement = 2, BREAK

Phase 4 — Sentinel check:
secondElement = 2 ≠ Integer.MIN_VALUE → continue

Phase 5 — Return:
[1, 2]  ✅
```

---

### Approach 1: Pseudocode

```plaintext
FUNCTION minAnd2ndMin_SortScan(arr):
    SET n = length of arr

    INSERTION SORT arr                          // ascending order

    SET result        = empty list
    SET firstElement  = arr[0]                  // minimum is at index 0
    SET secondElement = INTEGER.MIN_VALUE       // sentinel

    FOR EACH element i IN arr:
        IF i != firstElement THEN
            SET secondElement = i
            BREAK
        END IF
    END FOR

    IF secondElement == INTEGER.MIN_VALUE THEN
        ADD -1 TO result
        RETURN result
    END IF

    ADD firstElement  TO result
    ADD secondElement TO result
    RETURN result

END FUNCTION
```

---

## 7. Approach 2 — Single Pass (Optimised) ⚡

### Strategy

Track two variables — `min` and `secMin` — updating them as you scan the array **once**. No sorting. No mutation. $O(n)$ time.

### Core Logic

Three things can happen for each element `x`:

```
Case 1: x < min
        → x is the new overall minimum
        → old min gets demoted to second minimum
        → secMin = min, min = x

Case 2: x > min AND x < secMin AND x != min
        → x is better than current second minimum
        → secMin = x

Case 3: x == min OR x >= secMin
        → x is irrelevant, skip
```

Notice that `x != min` in Case 2 is what enforces **distinctness**. Without it, equal duplicates of the minimum could become `secMin`.

### Code

```java
public ArrayList<Integer> minAnd2ndMin(int[] arr) {
    int min    = Integer.MAX_VALUE;   // Tracks the overall minimum
    int secMin = Integer.MAX_VALUE;   // Tracks the second distinct minimum

    for (int x : arr) {
        if (x < min) {
            // x beats the current champion
            // demote old champion to runner-up first, then crown x
            secMin = min;
            min    = x;
        } else if (x < secMin && x != min) {
            // x doesn't beat champion but beats runner-up
            // and x is a different value from champion (distinct)
            secMin = x;
        }
        // else: x is irrelevant — skip
    }

    ArrayList<Integer> result = new ArrayList<>();

    if (secMin == Integer.MAX_VALUE) {
        // secMin was never updated → no second distinct value exists
        result.add(-1);
    } else {
        result.add(min);
        result.add(secMin);
    }

    return result;
}
```

---

### Why `Integer.MAX_VALUE` as the Initial Value?

Both `min` and `secMin` start at `Integer.MAX_VALUE` (`2,147,483,647`) — Java's largest possible integer.

**For `min`:** Any real value in the array will be smaller than `MAX_VALUE`, so the first element always replaces it. This removes the need for a special case to initialize `min = arr[0]`.

**For `secMin`:** If `secMin` is never updated from `MAX_VALUE` by the end of the loop, it means no second distinct value was ever found — the entire array had only one unique value. This is the "no result" signal, analogous to the sentinel in Approach 1 but safer because:

- `MAX_VALUE` is used as a starting "floor" — any valid second minimum will be less than it.
- Checking `secMin == Integer.MAX_VALUE` at the end cleanly detects the all-identical case.

> ⚠️ **Same sentinel risk applies:** If the array contains `Integer.MAX_VALUE`, `secMin` might never visibly update. In practice this is extremely rare, and the `boolean found` flag pattern (shown in Section 8) solves it completely.

---

### Step-by-Step Trace: Normal Case

**Input:** `[3, 1, 4, 1, 5, 2]`

| Step | x | Condition Met | min | secMin |
|------|---|--------------|-----|--------|
| Start | — | — | MAX | MAX |
| 1 | 3 | `3 < MAX` → Case 1 | 3 | MAX |
| 2 | 1 | `1 < 3` → Case 1 | 1 | 3 |
| 3 | 4 | `4 > 1`, `4 > 3` → skip | 1 | 3 |
| 4 | 1 | `1 == min` → skip | 1 | 3 |
| 5 | 5 | `5 > 1`, `5 > 3` → skip | 1 | 3 |
| 6 | 2 | `2 > 1`, `2 < 3`, `2 ≠ 1` → Case 2 | 1 | 2 |

**Result:** `[1, 2]` ✅

---

### Step-by-Step Trace: Duplicates at Minimum

**Input:** `[1, 1, 1, 2, 3]`

| Step | x | Condition Met | min | secMin |
|------|---|--------------|-----|--------|
| Start | — | — | MAX | MAX |
| 1 | 1 | `1 < MAX` → Case 1 | 1 | MAX |
| 2 | 1 | `1 == min` → skip | 1 | MAX |
| 3 | 1 | `1 == min` → skip | 1 | MAX |
| 4 | 2 | `2 > 1`, `2 < MAX`, `2 ≠ 1` → Case 2 | 1 | 2 |
| 5 | 3 | `3 > 1`, `3 > 2` → skip | 1 | 2 |

**Result:** `[1, 2]` ✅

---

### Step-by-Step Trace: All Identical

**Input:** `[5, 5, 5, 5]`

| Step | x | Condition Met | min | secMin |
|------|---|--------------|-----|--------|
| Start | — | — | MAX | MAX |
| 1 | 5 | `5 < MAX` → Case 1 | 5 | MAX |
| 2 | 5 | `5 == min` → skip | 5 | MAX |
| 3 | 5 | `5 == min` → skip | 5 | MAX |
| 4 | 5 | `5 == min` → skip | 5 | MAX |

`secMin == MAX_VALUE` → **Result:** `[-1]` ✅

---

### Step-by-Step Trace: Reverse Sorted (Worst Case Order)

**Input:** `[5, 4, 3, 2, 1]`

| Step | x | Condition Met | min | secMin |
|------|---|--------------|-----|--------|
| Start | — | — | MAX | MAX |
| 1 | 5 | `5 < MAX` → Case 1 | 5 | MAX |
| 2 | 4 | `4 < 5` → Case 1 | 4 | 5 |
| 3 | 3 | `3 < 4` → Case 1 | 3 | 4 |
| 4 | 2 | `2 < 3` → Case 1 | 2 | 3 |
| 5 | 1 | `1 < 2` → Case 1 | 1 | 2 |

**Result:** `[1, 2]` ✅

Notice that every element triggers Case 1 here — the minimum keeps getting replaced and the old minimum cascades into `secMin`. The algorithm handles it naturally without any special logic.

---

### Approach 2: Pseudocode

```plaintext
FUNCTION minAnd2ndMin_SinglePass(arr):
    SET min    = INTEGER.MAX_VALUE    // Will hold the smallest value
    SET secMin = INTEGER.MAX_VALUE    // Will hold the second distinct smallest

    FOR EACH element x IN arr:

        IF x < min THEN
            SET secMin = min          // Demote current champion to runner-up
            SET min    = x            // Crown new champion
        
        ELSE IF x < secMin AND x != min THEN
            SET secMin = x            // New runner-up (distinct from champion)
        
        END IF
        // else: x is not relevant

    END FOR

    SET result = empty list

    IF secMin == INTEGER.MAX_VALUE THEN
        ADD -1 TO result              // No second distinct value found
    ELSE
        ADD min    TO result
        ADD secMin TO result
    END IF

    RETURN result

END FUNCTION
```

---

### Approach 2: Complexity

| Property | Value |
|----------|-------|
| **Time Complexity** | $O(n)$ — single pass through array |
| **Space Complexity** | $O(1)$ extra |
| **Mutates Input?** | ❌ No |
| **Handles Duplicates?** | ✅ Yes |
| **Handles All-Identical?** | ✅ Yes |

---

## 8. The Sentinel Problem — And the Clean Fix

Both approaches use `Integer.MIN_VALUE` or `Integer.MAX_VALUE` as sentinels. Both break if those exact values appear in the input.

The cleanest fix for both is a **boolean flag** — it is always unambiguous regardless of the array's content:

```java
// Drop-in fix for Approach 1 (replaces Integer.MIN_VALUE sentinel)
boolean found = false;
int secondElement = 0;

for (int i : arr) {
    if (i != firstElement) {
        secondElement = i;
        found = true;
        break;
    }
}

if (!found) {
    small.add(-1);
    return small;
}
```

```java
// Drop-in fix for Approach 2 (replaces Integer.MAX_VALUE sentinel for secMin)
boolean found = false;
int min    = arr[0];
int secMin = 0;

for (int x : arr) {
    if (x < min) {
        if (!found || min != x) { found = true; secMin = min; }
        min = x;
    } else if ((!found || x < secMin) && x != min) {
        secMin = x;
        found  = true;
    }
}

if (!found) { result.add(-1); return result; }
```

In practice, the sentinel versions are accepted in most competitive programming contexts since `Integer.MIN_VALUE`/`MAX_VALUE` in an input array is virtually never the test case. But the boolean flag is the **production-correct** approach.

---

## 9. Approach Comparison — Full Picture

| | Approach 1 (Sort + Scan) | Approach 2 (Single Pass) |
|--|--------------------------|--------------------------|
| **Time** | $O(n^2)$ or $O(n \log n)$ | $O(n)$ ⚡ |
| **Space** | $O(1)$ | $O(1)$ |
| **Mutates input?** | ✅ Yes | ❌ No |
| **Passes needed** | 2 (sort + scan) | 1 |
| **Handles duplicates?** | ✅ | ✅ |
| **Handles all-identical?** | ✅ | ✅ |
| **Sentinel risk?** | ✅ Yes (`MIN_VALUE`) | ✅ Yes (`MAX_VALUE`) |
| **Readability** | Very easy to follow | Requires understanding Case 1/2 |
| **Best used when** | Learning, small `n` | Production, large `n` |

---

## 10. Common Mistakes & Pitfalls

| Mistake | Why it's wrong | Fix |
|--------|----------------|-----|
| Returning `arr[0]` and `arr[1]` directly | Fails when duplicates of min exist | Scan for first element `!= arr[0]` |
| `x != min` missing in Case 2 (Approach 2) | Duplicates of min become `secMin` incorrectly | Always check `x != min` alongside `x < secMin` |
| Forgetting to demote `min → secMin` before updating `min` | Lose the old minimum permanently | Always `secMin = min` before `min = x` |
| Using `0` or `-1` as sentinel | Valid integers — ambiguous | Use `boolean found` flag |
| `i < n` in outer sort loop | Accesses `arr[n]` → crash | Use `i < n-1` |
| Forgetting input is mutated (Approach 1) | Caller's array is permanently changed | Document it, or work on a copy |