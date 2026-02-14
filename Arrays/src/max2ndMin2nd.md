# 🏆 Algorithm: Get Second Order Elements (Second Max & Second Min)

---

## 1. Description

`getSecondOrderElements` finds two specific values from an integer array in a single combined result:

- **Second Maximum** — the largest value that is strictly less than the maximum
- **Second Minimum** — the smallest value that is strictly greater than the minimum

It returns them as `[secondMax, secondMin]`.

The function runs **two separate linear passes** — one to find the second minimum, one to find the second maximum — each using the **Two Champions** pattern seen in the previous problem.

---

## 2. Input & Output

| Feature | Type | Description |
|---------|------|-------------|
| Input `n` | `int` | Length of the array |
| Input `a` | `int[]` | Array of integers |
| Output | `int[]` | `[secondMax, secondMin]` |

### Output Cases

| Scenario | Input | Output |
|----------|-------|--------|
| Normal case | `[1, 2, 3, 4, 5]` | `[4, 2]` |
| Duplicates present | `[1, 1, 2, 3, 3]` | `[2, 2]` |
| Reverse sorted | `[5, 4, 3, 2, 1]` | `[4, 2]` |

> The problem assumes the input always has at least two **distinct** values, so `[-1]` edge case handling is not needed here.

---

## 3. Core Concept

This problem is two sub-problems running back to back:

```
Pass 1: Find min and secMin  →  [min, secMin] using ascending Two Champions
Pass 2: Find max and secMax  →  [max, secMax] using descending Two Champions
Return: [secMax, secMin]
```

The **Two Champions** pattern tracks two "title holders" simultaneously:
- A **Champion** (the best value found so far)
- A **Runner-up** (the second-best distinct value found so far)

When a new contender arrives, it either displaces the champion (old champion becomes runner-up), or it displaces the runner-up directly, or it's irrelevant.

---

## 4. Thinking & Mental Model

### 🧠 Pass 1 — "Coldest Temperature" Model (Second Minimum)

Imagine scanning daily temperatures looking for the coldest and second-coldest distinct temperatures ever recorded.

- If today is colder than the record → old record becomes second-coldest, today becomes the new record.
- If today is not the coldest but colder than the second-coldest record (and warmer than the coldest, so it's distinct) → today becomes the new second-coldest.
- Otherwise → irrelevant.

```
Stream: [3, 1, 4, 1, 5, 2]

x=3 → min=3,  secMin=MAX
x=1 → min=1,  secMin=3    (1 beats 3, 3 demoted)
x=4 → skip    (4 > secMin 3)
x=1 → skip    (1 == min, not distinct — because x > min is false)
x=5 → skip    (5 > secMin 3)
x=2 → secMin=2 (2 < secMin 3 AND 2 > min 1 — distinct ✅)

min=1, secMin=2
```

### 🧠 Pass 2 — "Hottest Temperature" Model (Second Maximum)

Same logic, but now you're hunting for the hottest and second-hottest.

- If today is hotter than the record → old record becomes second-hottest, today becomes the new record.
- If today is not the hottest but hotter than the second-hottest (and cooler than the hottest) → today becomes the new second-hottest.
- Otherwise → irrelevant.

```
Stream: [3, 1, 4, 1, 5, 2]

x=3 → max=3,  secMax=-1
x=1 → skip    (1 < max 3, 1 < secMax -1? No: 1 > -1 but 1 < max? No → but 1 is NOT > -1 AND < max 3... wait)
       Actually: 1 > secMax(-1) AND 1 < max(3) → secMax=1
x=4 → max=4,  secMax=3    (4 beats 3, 3 demoted)
x=1 → skip    (1 < secMax 3)
x=5 → max=5,  secMax=4    (5 beats 4, 4 demoted)
x=2 → skip    (2 < secMax 4)

max=5, secMax=4
```

---

## 5. Code Breakdown — Pass by Pass

---

### Pass 1 — Finding Second Minimum

```java
int min    = Integer.MAX_VALUE;
int secMin = Integer.MAX_VALUE;

for (int x : a) {
    if (x < min) {
        secMin = min;   // demote champion to runner-up
        min    = x;     // crown new champion
    } else if (x < secMin && x > min) {
        secMin = x;     // new runner-up (strictly between min and secMin)
    }
}
```

#### The Critical Condition: `x > min` vs `x != min`

This is the key difference from the previous `minAnd2ndMin` problem. Look carefully:

| Previous problem | This problem |
|-----------------|--------------|
| `x < secMin && x != min` | `x < secMin && x > min` |

Both enforce **distinctness** — they prevent a duplicate of the minimum from becoming `secMin`. But they express it differently:

- `x != min` — equality check. Blocks exact duplicates only.
- `x > min` — relational check. Blocks exact duplicates AND anything less than min (which can't happen here since `x < min` is already handled, so in practice they behave identically for valid input).

`x > min` is slightly more **semantically precise** for this problem because it explicitly says: "the second minimum must be strictly greater than the minimum" — which is the definition of a distinct second minimum.

#### Initialization with `Integer.MAX_VALUE`

Both start at `Integer.MAX_VALUE` so the first element in the array always beats `min` (since any real value < `MAX_VALUE`), and the cascade naturally sets `secMin = MAX_VALUE → old min`.

---

### Pass 2 — Finding Second Maximum

```java
int max    = a[0];
int secMax = -1;

for (int x : a) {
    if (x > max) {
        secMax = max;   // demote champion to runner-up
        max    = x;     // crown new champion
    } else if (x > secMax && x < max) {
        secMax = x;     // new runner-up (strictly between secMax and max)
    }
}
```

#### Why `max = a[0]` but `min = Integer.MAX_VALUE`?

This is an **inconsistency** in the original code. Both passes solve the same class of problem, but they initialize differently:

| | Pass 1 (min) | Pass 2 (max) |
|--|-------------|-------------|
| Champion init | `Integer.MAX_VALUE` | `a[0]` |
| Runner-up init | `Integer.MAX_VALUE` | `-1` |

**Pass 1** uses `Integer.MAX_VALUE` as a "floor" — any real array value will be smaller, so the first element always replaces it cleanly.

**Pass 2** uses `a[0]` as the starting champion and `-1` as the runner-up sentinel. This works for arrays with non-negative values, but:

> ⚠️ **Bug:** If the array contains negative numbers, `secMax = -1` is wrong. A valid `secMax` could be `-5`, which is less than `-1` and would never update the sentinel. The result would be an incorrect `-1` returned as `secMax`.

**Example of the bug:**
```
Input: [-3, -1, -5, -2, -4]

max = a[0] = -3, secMax = -1

x=-3: skip (== max)
x=-1: -1 > max(-3) → secMax=-3, max=-1
x=-5: -5 < secMax(-3) → skip
x=-2: -2 > secMax(-3) AND -2 < max(-1) → secMax = -2
x=-4: -4 < secMax(-2) → skip

Result: secMax=-2  ✅ (accidentally correct here)

But try: [-5, -3, -1, -2, -4]
max = a[0] = -5, secMax = -1

x=-5: skip (== max)
x=-3: -3 > max(-5) → secMax=-5, max=-3
x=-1: -1 > max(-3) → secMax=-3, max=-1
x=-2: -2 > secMax(-3)? No (-2 > -3 ✅) AND -2 < max(-1) ✅ → secMax=-2
x=-4: skip

Result: secMax=-2  ✅ (still works here)

Dangerous case: [−2, −1]
max = a[0] = -2, secMax = -1

x=-2: skip
x=-1: -1 > max(-2) → secMax = -2, max = -1

Result returned: secMax = -2 ✅

But: [−1, −2]
max = a[0] = -1, secMax = -1

x=-1: skip (== max)
x=-2: -2 > secMax(-1)? NO → skip

secMax stays -1  ❌  (should be -2)
```

The sentinel `-1` causes real failures on negative input. The fix is to initialize `secMax = Integer.MIN_VALUE` — the same symmetric approach used for `secMin`.

---

### The Return Statement

```java
return new int[] {secMax, secMin};
```

Returns a 2-element array: index 0 is `secMax`, index 1 is `secMin`. The order matches the problem's expected output format — second maximum first, second minimum second.

---

## 6. Full End-to-End Trace

**Input:** `a = [12, 35, 1, 10, 34, 1]`

**Pass 1 — Second Minimum:**

| x | Condition | min | secMin |
|---|-----------|-----|--------|
| Start | — | MAX | MAX |
| 12 | `12 < MAX` → Case 1 | 12 | MAX |
| 35 | `35 > 12` → skip | 12 | MAX |
| 1 | `1 < 12` → Case 1 | 1 | 12 |
| 10 | `10 < 12` AND `10 > 1` → Case 2 | 1 | 10 |
| 34 | `34 > 10` → skip | 1 | 10 |
| 1 | `1 == min` → skip | 1 | 10 |

`min=1, secMin=10`

**Pass 2 — Second Maximum:**

| x | Condition | max | secMax |
|---|-----------|-----|--------|
| Start | — | 12 | -1 |
| 12 | `12 == max` → skip | 12 | -1 |
| 35 | `35 > 12` → Case 1 | 35 | 12 |
| 1 | `1 < 12` → skip | 35 | 12 |
| 10 | `10 < 12` → skip | 35 | 12 |
| 34 | `34 > 12` AND `34 < 35` → Case 2 | 35 | 34 |
| 1 | `1 < 34` → skip | 35 | 34 |

`max=35, secMax=34`

**Result:** `[34, 10]` ✅

---

## 7. Algorithmic Approach

| Property | Value |
|----------|-------|
| **Strategy** | Two separate linear scans (Two Champions pattern) |
| **Time Complexity** | $O(n)$ — two passes of $O(n)$ each = $O(2n)$ = $O(n)$ |
| **Space Complexity** | $O(1)$ — only 4 variables: `min`, `secMin`, `max`, `secMax` |
| **Mutates Input?** | ❌ No |
| **Handles Duplicates?** | ✅ Yes |
| **Works with Negatives?** | ⚠️ Partially — `secMin` yes, `secMax` buggy (see Section 5) |

---

## 8. Pseudocode

```plaintext
FUNCTION getSecondOrderElements(n, a):

    // --- Pass 1: Find second minimum ---
    SET min    = INTEGER.MAX_VALUE
    SET secMin = INTEGER.MAX_VALUE

    FOR EACH element x IN a:
        IF x < min THEN
            SET secMin = min        // demote old champion
            SET min    = x          // crown new champion
        ELSE IF x < secMin AND x > min THEN
            SET secMin = x          // new runner-up (strictly above min)
        END IF
    END FOR

    // --- Pass 2: Find second maximum ---
    SET max    = a[0]
    SET secMax = -1                 // ⚠️ buggy for negative arrays

    FOR EACH element x IN a:
        IF x > max THEN
            SET secMax = max        // demote old champion
            SET max    = x          // crown new champion
        ELSE IF x > secMax AND x < max THEN
            SET secMax = x          // new runner-up (strictly below max)
        END IF
    END FOR

    RETURN [secMax, secMin]

END FUNCTION
```

---

## 9. Optimised Approach — Single Pass ⚡

The original code makes **two full passes** over the array. You can find all four values (`min`, `secMin`, `max`, `secMax`) in a **single pass** — cutting the number of iterations in half.

The logic for each element is just both sets of conditions evaluated together.

### Code

```java
public static int[] getSecondOrderElements(int n, int[] a) {
    int min    = Integer.MAX_VALUE;
    int secMin = Integer.MAX_VALUE;
    int max    = Integer.MIN_VALUE;   // ✅ Fixed: symmetric init, works for negatives
    int secMax = Integer.MIN_VALUE;   // ✅ Fixed: no more -1 sentinel

    for (int x : a) {

        // --- Minimum tracking ---
        if (x < min) {
            secMin = min;
            min    = x;
        } else if (x < secMin && x > min) {
            secMin = x;
        }

        // --- Maximum tracking ---
        if (x > max) {
            secMax = max;
            max    = x;
        } else if (x > secMax && x < max) {
            secMax = x;
        }
    }

    return new int[] {secMax, secMin};
}
```

### Why This Works in One Pass

Both the min-tracking and max-tracking blocks are **completely independent** of each other. They share no variables and neither result affects the other. So evaluating both for every element `x` produces identical results to running them in separate passes — just in half the iterations.

```
Original: iterate n times (min pass) + iterate n times (max pass) = 2n iterations
Optimised: iterate n times (both passes combined)               = n iterations
```

### Two Fixes Included

**Fix 1 — `max = Integer.MIN_VALUE` instead of `a[0]`**

Symmetric initialization. Any real array value will be greater than `MIN_VALUE`, so the first element always cleanly becomes the starting champion. No special-casing for `a[0]`.

**Fix 2 — `secMax = Integer.MIN_VALUE` instead of `-1`**

Eliminates the negative-number bug entirely. `-2,147,483,648` is smaller than any realistic array value, so any valid second maximum will correctly update it.

---

### Single Pass Trace

**Input:** `a = [12, 35, 1, 10, 34, 1]`

| x | min | secMin | max | secMax |
|---|-----|--------|-----|--------|
| Start | MAX | MAX | MIN | MIN |
| 12 | 12 | MAX | 12 | MIN |
| 35 | 12 | MAX | 35 | 12 |
| 1 | 1 | 12 | 35 | 12 |
| 10 | 1 | 10 | 35 | 12 |
| 34 | 1 | 10 | 35 | 34 |
| 1 | 1 | 10 | 35 | 34 |

**Result:** `[34, 10]` ✅ — same answer, half the work.

---

### Single Pass Pseudocode

```plaintext
FUNCTION getSecondOrderElements_optimised(n, a):

    SET min    = INTEGER.MAX_VALUE
    SET secMin = INTEGER.MAX_VALUE
    SET max    = INTEGER.MIN_VALUE
    SET secMax = INTEGER.MIN_VALUE

    FOR EACH element x IN a:

        // Minimum block
        IF x < min THEN
            SET secMin = min
            SET min    = x
        ELSE IF x < secMin AND x > min THEN
            SET secMin = x
        END IF

        // Maximum block (independent of minimum block)
        IF x > max THEN
            SET secMax = max
            SET max    = x
        ELSE IF x > secMax AND x < max THEN
            SET secMax = x
        END IF

    END FOR

    RETURN [secMax, secMin]

END FUNCTION
```

---

## 10. Approach Comparison

| | Original (2 passes) | Optimised (1 pass) |
|--|--------------------|--------------------|
| **Time** | $O(2n)$ = $O(n)$ | $O(n)$ ⚡ |
| **Space** | $O(1)$ | $O(1)$ |
| **Passes** | 2 | 1 |
| **Mutates input?** | ❌ No | ❌ No |
| **Works for negatives?** | ⚠️ `secMax` buggy | ✅ Fully correct |
| **Initialization** | Inconsistent (`a[0]` vs `MAX_VALUE`) | Symmetric (`MAX/MIN_VALUE`) |
| **Readability** | Easier to follow (separated concerns) | Slightly more compact |

> **Big-O is the same** — both are $O(n)$. But the single pass does **half the actual work at runtime**. For very large arrays, this is a meaningful constant-factor improvement.

---

## 11. Common Mistakes & Pitfalls

| Mistake | Why it's wrong | Fix |
|--------|----------------|-----|
| `secMax = -1` as sentinel | Breaks for negative arrays | Use `Integer.MIN_VALUE` |
| `max = a[0]` initialization | Inconsistent with `min` approach, fragile | Use `Integer.MIN_VALUE` symmetrically |
| Using `x != min` vs `x > min` | Both work here, but `x > min` is more semantically precise | Either is acceptable; be consistent |
| Forgetting to demote before updating | `secMin = min` must come before `min = x` | Always demote first, then crown |
| Running min and max in one `if/else if` chain | The two blocks are independent — chaining them creates wrong mutual exclusions | Keep them as separate `if` blocks |
| Swapping return order | Problem expects `[secMax, secMin]`, not `[secMin, secMax]` | Match the expected output format |