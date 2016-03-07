/* Basic Linked List implementation in the goLang programming language */

package main

import "fmt"
import "reflect"

type Node struct {
  value interface{}
  next *Node
}

type LinkedList struct {
  head *Node
}

/* Adds new node to the end of the list */
func (list LinkedList) add(toAdd interface {}) {
    if(reflect.TypeOf(list.head.value) != reflect.TypeOf(toAdd)) {
        fmt.Println("Cannot Add Value. Type does not match ")
        return
    }
    curNode := list.head
    for ;curNode.next != nil; {
        curNode = curNode.next
    }
    curNode.next = &Node{toAdd,nil}
}

func (list LinkedList) get(value interface{}) (interface{}) {
    curNode := list.head
    for ;curNode != nil; {
        if(curNode.value == value) {
            return &curNode
        }
        curNode = curNode.next
    }
    return nil;
}

/* Deletes first instance of node containing value. Requires Typeswitch */
func (list LinkedList) delete(value interface{}) {
    curNode := list.head
    if (list.head == value) {
        list.head.next = list.head
    }
    for ;curNode != nil; {
        if(curNode.next.value == value) {
            curNode.next = curNode.next.next
            return
        }
        curNode = curNode.next
    }
}
