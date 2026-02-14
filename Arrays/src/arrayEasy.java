import java.util.ArrayList;
import java.util.Arrays;

public class arrayEasy {
    static int largest(int[] arr){
        int max = arr[0];

        for(int element : arr){
            if(max < element){
                max = element;
            }
        }

        return max;
    }

    static ArrayList<Integer> minAnd2ndMin(int[] arr) {
        // code here

        int n = arr.length;

        // Arrays.sort(arr);
        for(int i=0;i<n-1;i++){
            for(int j=i+1;j>0;j--){
                if(arr[j] < arr[j-1]){
                    int temp = arr[j];
                    arr[j] = arr[j-1];
                    arr[j-1] = temp;
                }
            }
        }

        ArrayList<Integer> small = new ArrayList<>();

        int firstElement = arr[0];
        int secondElement = Integer.MIN_VALUE;

        for(int i : arr){
            if( i != firstElement){
                secondElement = i;
                break;
            }
        }

        if(secondElement == Integer.MIN_VALUE){
            small.add(-1);
            return small;
        }

        small.add(firstElement);
        small.add(secondElement);

        return small;

    }
    static ArrayList<Integer> betterMinAnd2ndMin(int[] arr){

        int min = Integer.MAX_VALUE;
        int secMin = Integer.MAX_VALUE;

        for(int x : arr){
            if(x < min){
                secMin = min;
                min = x;
            }else if(x < secMin && x!= min){
                secMin = x;
            }
        }

        ArrayList<Integer> small = new ArrayList<>();

        if(secMin == Integer.MAX_VALUE){
            small.add(-1);
            return small;
        }else{
            small.add(min);
            small.add(secMin);
            return small;
        }
    }
    static int[] max2ndMin2nd(int[] arr){
        int min = Integer.MAX_VALUE;
        int secMin = Integer.MAX_VALUE;

        for(int x : arr){
            if(x < min){
                secMin = min;
                min = x;
            }else if(x < secMin && x > min){
                secMin = x;
            }
        }

        int max = arr[0];
        int secMax = -1;

        for(int x : arr){
            if(x > max){
                secMax = max;
                max = x;
            }else if(x > secMax && x < max){
                secMax = max;
            }
        }

        return new int[] {secMax,secMin};
    }

    static boolean checkIfArrSortedAndRotated(int[] arr){
        int n = arr.length;
        int cnt = 0;
        for(int i = 0; i<n;i++){
            if(arr[i] > arr[(i+1)%n]) cnt++;
        }

        return cnt <= 1;
    }

    static int removeDuplicate(int[] arr){
        int slow = 0;

        for(int fast = 1; fast<arr.length; fast++){
            if(arr[fast] != arr[slow]){
                slow++;
                arr[slow] = arr[fast];
            }
        }

        return slow+1;
    }

    static void rotate(int[] arr, int k) {

        System.out.println(Arrays.toString(arr));

    }
    static int[] rotateBy1(int[] arr){
        int n = arr.length;
        int temp = arr[0];
        for(int i = 0; i<n-1;i++){
            // int idx = ((i-1)%n + n)%n;
            // int temp1 = arr[idx]; // 6
            // arr[idx] = temp;
            // temp = temp1;
            arr[i] = arr[i+1];
        }

        arr[n-1] = temp;
        return arr;
    }

    public static void main(String[] args) {
        int[] arr = {1,2,3,4,5,6,7};
//        ArrayList<Integer> result = betterMinAnd2ndMin(arr);
//        System.out.println(largest(arr));
//        System.out.println(minAnd2ndMin(arr));
//        int[] res = max2ndMin2nd(arr);
//        System.out.println(Arrays.toString(res));
//        System.out.println(checkIfArrSortedAndRotated(arr));
        rotate(arr,3);



//        for(int x : result){
//            System.out.print(x + " ");
//        }

    }
}
