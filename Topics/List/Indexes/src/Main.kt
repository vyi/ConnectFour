fun solution(products: List<String>, product: String) {
    // put your code here
    for (i in products.indices) {
        if (product in products[i]) {
            print("$i ")
        }
    }
}