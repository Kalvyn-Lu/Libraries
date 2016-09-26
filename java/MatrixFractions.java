package com.google.challenges;
import java.util.Arrays;


public class Answer {
    public static int[] answer(int[][] m) {
      	MatrixOrderCount moc = toTransitionMatrix(m);
      	Fraction[] firstToTerm = findLimitingMatrix(moc)[0];
      	int LCD = findLCD(firstToTerm);
      	int[] answer = new int[moc.count + 1];
      	
        for(int  i = 0; i < answer.length - 1; i++) {
			    answer[i] = firstToTerm[i].numerator * (LCD / firstToTerm[i].denominator);
        }
      
      	answer[answer.length - 1] = LCD;
      	return answer;
    }

    public static int findLCD(Fraction[] fractions) {
      int curLCD = 0;
      for(int i = 0 ; i < fractions.length; i++) {
        for(int j = 0; j < fractions.length; j++) {
          int LCD = Fraction.findLCM(fractions[i].denominator, fractions[j].denominator);
          if(LCD > curLCD) {
            curLCD = LCD;
          }
        }
      }
      return curLCD;
    }
    
    public static Fraction[][] transposeMatrix(Fraction[][] matrix) {
      int rows = matrix.length;
      int columns = matrix[0].length;
      Fraction[][] transpose = new Fraction[rows][columns];
      for(int i = 0; i < rows; i++) {
        for(int j = 0; j < columns; j++) {
          transpose[i][j] = matrix[j][i];
        }
      }
      
      return transpose;
    }
    
    public static Fraction determinant(Fraction[][] matrix) {
      if(matrix.length == 1) return matrix[0][0];
      if(matrix.length == 2) {
        return Fraction.subtract(Fraction.multiply(matrix[0][0], matrix[1][1]), Fraction.multiply(matrix[0][1], matrix[1][0]));
      }
      
      Fraction sum = new Fraction(0,1);
      for(int i = 0; i < matrix.length; i++) {
        Fraction alternator = new Fraction(i % 2 == 0 ? 1 : -1, 1);
        
        Fraction addend = Fraction.multiply(alternator, Fraction.multiply(matrix[0][i], determinant(subMatrix(matrix, 0, i))));
        
        Fraction.add(sum, addend);
      }
      
      return sum;
    }
    
    public static Fraction[][] subMatrix(Fraction[][] matrix, int excludingRow, int excludingCol) {
      Fraction[][] subMatrix = new Fraction[matrix.length - 1][matrix.length - 1];
      
      int newRowCount = -1;
      
      for(int i = 0; i < matrix.length; i++) {
        if(i == excludingRow) {
          continue;
        }
        newRowCount++;
        int newColCount = -1;
        for(int j = 0; j < matrix.length; j++) {
          if(j == excludingCol) {
            continue;
          }
          subMatrix[newRowCount][++newColCount] = matrix[i][j];
        }
      }
      return subMatrix;
    }
    
    public static Fraction[][] cofactor(Fraction[][] matrix) {
      Fraction[][] cofactor = new Fraction[matrix.length][matrix[0].length];
      for(int i = 0; i < matrix.length; i++) {
        for(int j = 0; j < matrix[0].length; j++) {
          Fraction sign = new Fraction((i + j) % 2 == 0 ? 1 : -1, 1);
          cofactor[i][j] = Fraction.multiply(sign, determinant(subMatrix(matrix, i, j)));
        }
      }
      return cofactor;
    }
    
    public static Fraction[][] inverseMatrix(Fraction[][] matrix) {
      return scalarMultiply(transposeMatrix(cofactor(matrix)), Fraction.divide(new Fraction(1,1), determinant(matrix)));
    }
    
    public static Fraction[][] scalarMultiply(Fraction[][] matrix, Fraction scalar) {
      Fraction[][] product = new Fraction[matrix.length][matrix[0].length];
      for(int i = 0; i < matrix.length; i++) {
        for(int j = 0; j < matrix[0].length; j++) {
          product[i][j] = Fraction.multiply(scalar, matrix[i][j]);
          product[i][j].simplify();
        }
      }
      
      return product;
    }
    
    public static Fraction[][] findLimitingMatrix(MatrixOrderCount moc) {
        int nonTerminals = moc.matrix.length - moc.count;
        Fraction[][] R = new Fraction[nonTerminals][moc.count];
        for(int i = 0; i < nonTerminals; i++){
            R[i] = Arrays.copyOfRange(moc.matrix[i + moc.count], 0, moc.count);
        }
        
        Fraction[][] Q = new Fraction[moc.matrix[0].length - moc.count][moc.matrix[0].length - moc.count];
        
        for(int i = 0; i < nonTerminals; i++) {
            Q[i] = Arrays.copyOfRange(moc.matrix[i + moc.count], moc.count, moc.matrix[0].length);
        }
      
      	Fraction[][] identity = createIdentity(Q.length);
        
        Fraction[][] F = inverseMatrix(matrixSubtract(identity, Q));
        
        return matrixMultiply(F,R);
    }
  
    public static Fraction[][] matrixSubtract(Fraction[][] a, Fraction[][] b) {
      Fraction[][] difference = new Fraction[a.length][a[0].length];
      for(int i = 0; i < a.length; i++) {
        for(int j = 0; j < a[0].length; j++) {
          difference[i][j] = Fraction.subtract(a[i][j],b[i][j]);
        }
      }

      return difference;
    }
  
    public static Fraction[][] createIdentity(int length) {
      Fraction[][] identity = new Fraction[length][length];
      for(int i = 0; i < length; i++) {
        for(int j = 0; j < length; j++) {
          if(i == j) {
            identity[i][j] = new Fraction(1,1);
          } else {
            identity[i][j] = new Fraction(0,1);
          }
        }
      }
      return identity;
    }
    
    public static Fraction[][] matrixMultiply(Fraction[][] a, Fraction[][] b) {
        int aRows = a.length;
        int aColumns = a[0].length;
        int bRows = b.length;
        int bColumns = b[0].length;
        Fraction[][] product = new Fraction[aRows][bColumns];
        
        for(int i = 0; i < aRows; i++) {
            for(int j = 0; j < bColumns; j++) {
                product[i][j] = new Fraction(0,1);
                for(int k = 0; k < aColumns; k++) {
                    product[i][j] = Fraction.add(product[i][j],Fraction.multiply(a[i][k], b[k][j]));
                }
            }
        }
        
        return product;
    }
    
    public static class MatrixOrderCount {
        Fraction[][] matrix;
        int[] order;
        int count;
        
        public MatrixOrderCount (Fraction[][] matrix, int[] order, int count) {
            this.matrix = matrix; //transition matrix
            this.order = order; // Order of states
            this.count = count; // # of terminal states
        }
    }
    
    public static MatrixOrderCount toTransitionMatrix(int[][] stepsMatrix) {
        int size = stepsMatrix.length;
        int terminalCount = 0; //Terminal States placed before non-terminals for standard form
        int nonTerminalCount = 0;
        Fraction[][] transitionMatrix = new Fraction[size][size];
        int[] order = new int[size];
        
        for(int i = 0; i < size; i++) {
            int sum = arraySum(stepsMatrix[i]);
            
            if(sum == 0) { //Absorbing state
                for(int j = 0; j < size; j++) {
                    transitionMatrix[terminalCount][j] = new Fraction(0,0);
                }
                
                transitionMatrix[terminalCount][terminalCount] = new Fraction(1,1);
                order[terminalCount] = i;
                terminalCount++;
                
            }
        }
      
        for(int i = 0; i < stepsMatrix.length; i++) {
          int sum = arraySum(stepsMatrix[i]);
            if(sum != 0) {
              order[nonTerminalCount + terminalCount] = i;
            }
          nonTerminalCount++;
        }
      
        for(int i = 0; i < stepsMatrix.length; i++) {
          int sum = arraySum(stepsMatrix[i]);
          if(sum != 0) {
            for(int j = 0; j < stepsMatrix[0].length; j++) {
              transitionMatrix[terminalCount + i][j] = new Fraction(stepsMatrix[i][order[j]], sum);
            }
          }
        }
        
        return new MatrixOrderCount(transitionMatrix, order, terminalCount);
    }
    
    public static int arraySum(int[] array) {
        int sum = 0;
        for(int element : array) {
            sum += element;
        }
        
        return sum;
    }
     
    static class Fraction {
        int numerator, denominator;
        
        public Fraction(int numerator, int denominator) {
            this.numerator = numerator;
            this.denominator = denominator;
        }
        
        public static Fraction multiply(Fraction a, Fraction b) {
            return new Fraction(a.numerator * b.numerator, a.denominator * b.denominator);
        }
        
        public static Fraction divide(Fraction a, Fraction b) {
            return new Fraction(a.numerator * b.denominator, a.denominator * b.numerator);
        }
        
        public static Fraction add(Fraction a, Fraction b) {
            int lcm = findLCM(a.denominator, b.denominator);
            int augendNum = a.numerator * lcm/a.denominator;
            int addendNum = b.numerator * lcm/b.denominator;
            Fraction sum = new Fraction(augendNum + addendNum, lcm);
            sum.simplify();
            return sum;
        }
        
        public static Fraction subtract(Fraction a, Fraction b) {
            int lcm = findLCM(a.denominator, b.denominator);
            int minuendNum = a.numerator * lcm/a.denominator;
            int subtrahendNum = b.numerator * lcm/b.denominator;
            Fraction difference = new Fraction(minuendNum - subtrahendNum, lcm);
            difference.simplify();
            return difference;
        }
        
        public void simplify() {
            int gcf = findGCF(numerator, denominator);
            numerator /= gcf;
            denominator /= gcf;
          
          if(denominator < 0) {
            denominator *= -1;
            numerator *= -1;
          }
        }
        
        //Euclid's algorithm to find Greatest Common Factor
        public static int findGCF(int a, int b) {
            if(b == 0) return a;
            return findGCF(b, a % b);
        }
        
        //LowestCommonMultiple
        public static int findLCM(int a, int b) {
            return (a * b)/ findGCF(a, b);
        }
        
        public String toString() {
            return numerator + "/" + denominator;
        }
    }
}