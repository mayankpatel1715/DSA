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

    public static void main(String[] args) {
        int[] arr = {5,32,6,1,31,6,61,123,2};

        System.out.println(largest(arr));

    }
}
