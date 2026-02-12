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
    
    public static void main(String[] args) {
        int[] arr = {2,4,3,5,6};
        ArrayList<Integer> result = betterMinAnd2ndMin(arr);
//        System.out.println(largest(arr));
//        System.out.println(minAnd2ndMin(arr));

        for(int x : result){
            System.out.print(x + " ");
        }

    }
}
