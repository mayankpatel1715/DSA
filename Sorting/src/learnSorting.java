import java.util.Arrays;

public class learnSorting {
    static void bubbleSortLearn(int[] arr) {

        int n = arr.length;
        boolean swapped = false;

        for (int i = 0; i < n - 1; i++) {
            for (int j = 1; j <= n - 1 - i; j++) {
                if (arr[j] < arr[j - 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j - 1];
                    arr[j - 1] = temp;
                    swapped = true;
                }
            }
            if(!swapped) break;
        }

    }

    static void selectionSortLearn(int[] arr){
        int n = arr.length;
        for(int i = 0; i < n-1; i++){
            int last = arr.length - i - 1;
            int max = 0;
            for (int j = 1; j <= last; j++) {
                if(arr[max] < arr[j]) max = j;
            }

            int temp = arr[last];
            arr[last] = arr[max];
            arr[max] = temp;
        }
    }



    public static void main(String[] args) {
        int[] arr = {4,5,1,2,3};

//        bubbleSortLearn(arr);
        selectionSortLearn(arr);
        System.out.println(Arrays.toString(arr));

    }
}