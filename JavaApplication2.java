/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package si4_lab3;
import java.util.concurrent.ThreadLocalRandom;
/**
 *
 * @author Kevin
 */
public class HugeInteger {
    private int size;
    private Node head;
    private Node last;
    private boolean isNegative;
    
    //creates a random HugeInteger of n digits, the first
    //digit being different from 0; n has to be larger or equal to 1
    public HugeInteger(int n) throws IndexOutOfBoundsException{
        
        if(n<1){throw new IndexOutOfBoundsException();}
        int[] maxInt= {9999,9,99,999};
        int[] minInt= {1000,1,10,100};
        
        head = new Node();
        size=n/4;
        isNegative = false;
        
        Node MS = new Node();
                
        int mostSigLength = n%4;
        if(mostSigLength !=0){size++;}
        
        MS.setData( ThreadLocalRandom.current().nextInt(
            minInt[mostSigLength], maxInt[mostSigLength] + 1), null, head);
        head.setNext(MS);
        last =MS;
        
        int newNodesCount = n/4;
        if(mostSigLength ==0){newNodesCount--;}
        
        for(int i=0;i<newNodesCount;i++){
            Node x = new Node(ThreadLocalRandom.current().nextInt //rand int from 0 to 9999
            (0, 9999 + 1),null,MS);
            MS.setNext(x);
            MS = MS.next;
            last =MS;
        }

    }
    
    //creates a HugeInteger from the decimal String representation val.
    //The string contains an optional minus sign at the beginning 
    //followed by one or more decimal digits. No other 
    //characters are allowed in the string
    public HugeInteger(String val){
        int length = val.length();

        if(val.charAt(0)=='-'){length--;}
        int mostSigLength = length%4;
        size = length/4;
        head = new Node();
        isNegative = false;
        
        if(mostSigLength !=0){size++;}
        if(mostSigLength ==0){mostSigLength=4;}
        if(val.charAt(0)=='-'){mostSigLength++;isNegative=true;}
        
        
        Node MS = new Node();
        String Substring = val.substring(0,mostSigLength);
        int ValueofSubstring= Integer.parseInt(Substring);
        MS.setData(ValueofSubstring,null,head);
        head.setNext(MS);
        last =MS;        
        
        for(int i=mostSigLength;i<length;i=i+4){
            if(val.charAt(0)=='-' && i==0){
                Substring = val.substring(i,i+5);
                i++;
            }
            else{Substring = val.substring(i,i+4);}
            ValueofSubstring= Integer.parseInt(Substring);
            Node x = new Node(ValueofSubstring,null,MS);
            MS.setNext(x);
            MS = MS.next;
            last =MS;
        }
        
    }
    
    // Returns a new HugeInteger representing
    //the sum of this HugeInteger and h
    public HugeInteger add(HugeInteger h){
        Node p1 = this.last;
        Node p2 = h.last;
        HugeInteger Sum = new HugeInteger("1");//holds new sum
        if(this.isNegative && !h.isNegative){
            this.negate();
            Sum = h.subtract(this); //-(a-b) = b-a
            this.negate();
            return Sum;
        }
        if(!this.isNegative && h.isNegative){
            h.negate();
            Sum = this.subtract(h);
            h.negate();
            return Sum;
        }
        if(this.isNegative && h.isNegative){
            this.negate();h.negate();
            Sum = this.add(h);
            Sum.negate();
            this.negate();h.negate();
            return Sum;
        }
        
        
        Node LS = Sum.last;
        boolean Isfirst =true;
        Sum.size =0;
        
        int carryOn = 0;
        int nodeSum = 0;
        
        while(p1 != this.head || p2 != h.head || carryOn == 1){
            nodeSum = p1.num + p2.num + carryOn;
            
            carryOn = 0;
            if(nodeSum >= 10000){
                nodeSum = nodeSum - 10000;
                carryOn = 1;
            }
            if(Isfirst==true){//first part is different since the first node
                //already exists, thus no memory needed to allocate
                LS.setNumber(nodeSum);                
                //Node x1 = new Node(nodeSum,null,Sum.head);
                //Sum.last = x1;
                Isfirst=false;
                Sum.size++;
            }
            else{
                Node x = new Node(nodeSum,LS,Sum.head);//int,next,prev
                LS.setPrev(x);
                LS = LS.prev;
                Sum.size++;
                Sum.head.next=x;
            }
            //update prev of the previously looked at node
            //update size of Sum
            
            if(p1 != this.head){p1 = p1.prev;}
            if(p2 != h.head){p2 = p2.prev;}
        }
        
        
        return Sum;
    }
    
    //Returns a new HugeInteger representing 
    //the difference between this HugeInteger and h
    public HugeInteger subtract(HugeInteger h){
        HugeInteger Diff = new HugeInteger("1");
        
        if(this.isNegative && !h.isNegative){
            this.negate();
            Diff = this.add(h);
            Diff.negate();
            this.negate();
            return Diff;
        }
        if(!this.isNegative && h.isNegative){//h is negative
            h.negate();
            Diff = this.add(h);
            h.negate();
            return Diff;
        }
        if(this.isNegative && h.isNegative){
            this.negate();h.negate();
            Diff = this.subtract(h);
            Diff.negate();
            this.negate();h.negate();
            return Diff;
        }
        
        if(this.compareTo(h)==-1){
            Diff = h.subtract(this);
            Diff.negate();
            return Diff;
        }
        
        Node p1 = this.last;
        Node p2 = h.last;
        
        
        Node LS = Diff.last;
        boolean Isfirst =true;
        boolean Islast = false;
        Diff.size =0;
        
        int carryOn = 0;
        int nodeDiff = 0;
        
        while(p1 != this.head || p2 != h.head || carryOn == -1){
            
            nodeDiff = p1.num - p2.num + carryOn;
            carryOn = 0;
            Islast = ( (p1==this.head && p2 == h.head.next) 
                    || (p1==this.head.next && p2 == h.head)
                    || (p1==this.head.next && p2 == h.head.next));
            
            if(nodeDiff < 0 && p1.num < p2.num && !(Islast)){
                nodeDiff = 10000 + nodeDiff;
                carryOn = -1;
            }
            
            
            if(Isfirst==true){//first part is different since the first node
                //already exists, thus no memory needed to allocate
                LS.setNumber(nodeDiff);                
                //Node x1 = new Node(nodeDiff,null,Diff.head);
                //Diff.last = x1;
                Isfirst=false;
                Diff.size++;
            }
            else if(!Islast || nodeDiff !=0){
                Node x = new Node(nodeDiff,LS,Diff.head);//int,next,prev
                LS.setPrev(x);
                LS = LS.prev;
                Diff.size++;
                Diff.head.next=x;
            }
            //update prev of the previously looked at node
            //update size of Diff
            
            if(p1 != this.head){p1 = p1.prev;}
            if(p2 != h.head){p2 = p2.prev;}
        }
        
        
        return Diff;
    } 
    
    //Returns a new HugeInteger representing
    //the product between this HugeInteger and h.
    public HugeInteger multiply(HugeInteger h){

        Node p1;
        Node p2;
        HugeInteger Prod = new HugeInteger("1");
        if(this.isNegative && !h.isNegative){
            this.negate();
            Prod = this.multiply(h);
            Prod.negate();
            this.negate();
            return Prod;
        }
        if(!this.isNegative && h.isNegative){
            h.negate();
            Prod = this.multiply(h);
            Prod.negate();
            h.negate();
            return Prod;
        }
        if(this.isNegative && h.isNegative){
            this.negate();h.negate();
            Prod = this.multiply(h);
            this.negate();h.negate();
            return Prod;
        }
        
        int Mag1 = 1;//order of magnitude of current node
        int Mag2 = 1;//also distance from end of number
       
        int SIZE = this.size+h.size;
        int[] Sums= new int[SIZE];
        int partialProd;
        
        for(p1 = this.last;p1 != this.head;p1=p1.prev){
            Mag2=1;
            p2 = h.last;
            while(p2 != h.head && Mag1>=Mag2){
                partialProd=p1.num*p2.num;
                Sums[Mag1+Mag2-2]=Sums[Mag1+Mag2-2] + partialProd%10000;
                Sums[Mag1+Mag2-1]=Sums[Mag1+Mag2-1] + partialProd/10000;
                Mag2++;
                p2=p2.prev;
            }
            Mag1++;

        }
        Mag1=1;Mag2=1;
        for(p2 = h.last;p2 != h.head;p2=p2.prev){
            Mag1=1;
            p1=this.last;
            while(p1 != h.head && Mag1<Mag2){
                partialProd=p1.num*p2.num;
                Sums[Mag1+Mag2-2]=Sums[Mag1+Mag2-2] + partialProd%10000;//second part
                Sums[Mag1+Mag2-1]=Sums[Mag1+Mag2-1] + partialProd/10000;//first part
                Mag1++;
                p1=p1.prev;
            }
            Mag2++;
        }
                
        //Handles all the carry on for each word
        //And builds the HugeInteger Prod
        p1=Prod.last;
        for(int i=0;i<SIZE;i++){
            if(i!=SIZE-1){
                Sums[i+1]=Sums[i+1]+Sums[i]/10000;
                Sums[i]=Sums[i]%10000;
            }
            if(i==0){
                p1.setNumber(Sums[i]);
            }
            else if(i!=SIZE && Sums[i] !=0){
                Node x = new Node(Sums[i],p1,Prod.head);//int,next,prev
                p1.setPrev(x);
                p1 = p1.prev;
                Prod.size++;
                Prod.head.next=x;
                
            }
        }
        
        return Prod;
        
    }
    
    //Returns -1 if this HugeInteger is less than h,
    //1 if this HugeInteger is larger than h, and 
    //0 if this HugeInteger is equal to h.
    public int compareTo(HugeInteger h){
        
        if(this.isNegative && !(h.isNegative)){ return -1;}
        if(!(this.isNegative) && h.isNegative){ return 1;}
        //if here, HugeIntegers have same sign
        
        if(this.isNegative && this.size > h.size){return -1;}
        if(this.isNegative && this.size < h.size){return 1;}
        
        if(!(this.isNegative) && this.size < h.size){return -1;}
        if(!(this.isNegative) && this.size > h.size){return 1;}
        //if here, this and h have same sign and size
        
        Node p1 = this.head.next;
        Node p2 = h.head.next;
        
        for(int i =0; i< this.size;i++){
            if(i==0){
                if(p1.num < p2.num){return -1;}
                if(p1.num > p2.num){return 1;}
                
                
            }
            else{
                if(this.isNegative && p1.num > p2.num ){return -1;}
                if(this.isNegative && p1.num < p2.num){return 1;}
        
                if(!(this.isNegative) && p1.num  < p2.num){return -1;}
                if(!(this.isNegative) && p1.num  > p2.num){return 1;}
            }
            p1 = p1.next;
            p2 = p2.next;
        }
        
        return 0;
    }
    
    //Returns a string representing the sequence of digits
    //corresponding to the decimal representation of this HugeInteger
    public String toString(){
        Node x = this.head.next;
        String str = "";
        Integer nodeInt;
        
        for(int i =0;i<this.size;i++){
            nodeInt = x.num;
            if(x == this.head.next){
                str = str + nodeInt.toString();
                x=x.next;
            }
            else{
                if(nodeInt <10){
                   str = str + "000" + nodeInt.toString(); 
                }
                else if(nodeInt<100){
                    str = str + "00" + nodeInt.toString();
                }
                else if(nodeInt<1000){
                    str = str + "0" + nodeInt.toString();
                }
                else{
                    str = str + nodeInt.toString();
                }
                x=x.next;
            }
        }
        return str;
    } 
    
    //multiplies the number by -1
    //Used mainly to avoid other methods from having to
    //deal with negative numbers
    public void negate(){
        this.head.next.setNumber(-1*this.head.next.num);
        if(this.isNegative == true){
            this.isNegative = false;
        }
        else if(this.isNegative == false){
            this.isNegative = true;
        }
    }
}
