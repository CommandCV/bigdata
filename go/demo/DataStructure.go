package main

import "fmt"

func main() {
	// array
	println("Array")
	arr1 := [3]int{1, 2, 3}
	arr2 := [...]int{1, 2, 3}
	fmt.Println(arr1)
	fmt.Println(arr2)

	// slice
	println("Slice")
	slice1 := []int{1, 2, 3}
	slice2 := make([]int, 3)
	slice3 := slice2[0:3]
	fmt.Println(slice1)
	fmt.Println(slice2)
	fmt.Println(slice3)

	// hash
	println("Hash")
	hash1 := map[string]int{}
	hash1["1"] = 1
	hash1["2"] = 2
	hash1["3"] = 3
	hash2 := make(map[string]int, 3)
	fmt.Println(hash1)
	fmt.Println(hash2)

	// string
	println("String")
	str1 := "hello world"
	str2 := `"hello world"`
	fmt.Println(str1)
	fmt.Println(str2)
}
