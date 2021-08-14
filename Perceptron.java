import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

/**
 * @author Helen Li
 * @version April 30 2020
 *
 * A program that models a Perceptron with configurable number of input
 * nodes, number of hidden layers with different number of nodes in each layer
 * and any number of nodes in the output layer.
 *
 * File requirements:
 * A file named dimensions.txt, with an integer on each line. First line with the number
 * of input nodes, second line with the number of hidden layers, the next few lines
 * (if there are more than one hidden layer) with the number of nodes in each hidden
 * layer, and the last line with the number of output nodes.
 * A file named weights.txt, with a value on each line. It should start with
 * w[0][0][0], increments the third index, and then the second index, and lastly the first.
 * A file (name is decided by the user), with each set of input on one line, separated
 * by white spaces. The last value of each line should be the T value for the respective set.
 * A file (name is decided by the user) with all relevant values needed for training,
 * see more descriptions about the file formatting in the block comment of the main method.
 *
 * Perceptron   - constructor to create a Perceptron object
 * setA         - initializes the 2D instance variable array a with a proper size
 * setWeights   - initializes the 3D instance variable array w with a proper size and fills it with weights from weights.txt
 * output       - computes the dot product sums and uses those values to fill the instance variable array a
 * threshold    - takes in a value, passes it into a mathematical function as the x variable, and returns the output
 * derivative   - calculates the derivative of the threshold function at the value given by the parameter
 * calcError    - calculates the error value by squaring the difference between T value and outputs and dividing it by 2
 * run          - tests the Perceptron by setting up a and w, reading inputs, computing outputs, and calculating error values
 * randomizeW   - fills the 3D instance variable array w with randomized weights, limited without bounds given by parameter
 * gradient     - implemented with the back propagation algorithm and adjusts weights using gradient descent to minimize error
 * train        - trains the Perceptron using the gradient descent method implemented with back propagation
 * main         - static method that serves as a tester for the Perceptron class to train/run the Perceptron
 */
public class Perceptron
{
   private double[] inputNodes;               // stores the values of the nodes in the input activation layer
   private int numHiddenLayer;                // the number of hidden activation layers
   private int numTotalLayers;                // the total number of layers
   private int[] numNodes;                    // stores the number of nodes in each layer of the Perceptron
   private double[][] a;                      // nodes array: first index as activation layer, second index as position of node in layer
   private double[][][] w;                    // weights array: first index as activation layer, second index as position of node
                                              // before the weight, and third index as position of node after weights

   /**
    * Constructs a new Perceptron with the given number of input activation nodes,
    * the number of hidden nodes in each hidden activation layer, and number of output nodes.
    *
    * @param numInputNodes      specifies the number of the nodes in the input layer
    * @param hiddenLayerNodes   specifies the number of hidden nodes in each hidden layer
    * @param outputNodes        specifies the number of nodes in the output layer
    */
   public Perceptron(int numInputNodes, int[] hiddenLayerNodes, int outputNodes)
   {
      this.numHiddenLayer = hiddenLayerNodes.length;  // the number of hidden layers is the size of the hiddenLayerNodes array
      inputNodes = new double[numInputNodes];         // creates a 1D array representing the input layer, size is numInputNodes

      numTotalLayers = 1 + numHiddenLayer + 1;        // total layers is numHiddenLayers + 2, adding the input and output layers
      numNodes = new int[numTotalLayers];             // numNodes stores the number of nodes in each layer
      numNodes[0] = numInputNodes;                    // first layer is the input nodes, filled by numInputNodes
      numNodes[numTotalLayers - 1] = outputNodes;     // last layer is the output nodes, filled by outputNodes
      for (int n = 1; n < numTotalLayers - 1; n++)    // iterates over the all hidden layers, from layer 1 to numTotalLayers - 2
         numNodes[n] = hiddenLayerNodes[n - 1];       // transfers the numbers stored in array hiddenLayerNodes to numNodes
   } // public Perceptron(int numInputNodes, int[] hiddenLayerNodes, int outputNodes)

   /**
    * Initializes the 2D instance variable array called a with a proper size.
    *
    * The proper size of array a should be [number of activation layers in total]
    * [varies based on how many nodes are in each activation layer]. The method traverses through
    * the instance variable array numNodes to figure out the number of nodes in each
    * layer to make sure that the 2D array a has the proper size.
    */
   public void setA()
   {
      a = new double[numTotalLayers][0];         // first index of array a is the total number of layers

      for (int n = 0; n < numTotalLayers; n++)   // iterates over all activation layers
         a[n] = new double[numNodes[n]];         // the size of array a for the current layer is the number of nodes in it
   }

   /**
    * Initializes the 3D instance variable called w with a proper size and fills it with
    * values of weights read from a file named weights.txt using a BufferedReader.
    *
    * The proper size of array w should be [number of activation layers with another layer on its righthand side]
    * [the number of nodes in the layer before the weights][the number of nodes in the layer after the weights].
    *
    * @throws IOException   thrown to indicate a failure in Input/Output operations while attempting to read
    */
   public void setWeights() throws IOException
   {
      BufferedReader readW = new BufferedReader
                             (new FileReader("weights.txt"));      // creates a BufferedReader to read weights from the weights.txt

      w = new double[numHiddenLayer + 1][0][0];                    // total weights layers is numHiddenLayer + 1 (add the layer from inputs)

      for (int n = 0; n < numHiddenLayer + 1; n++)                 // iterates over all layers of weights
      {
         w[n] = new double[numNodes[n]][0];                        // second index of w array is the number of nodes in the layer left of weights

         for (int k = 0; k < numNodes[n]; k++)                     // iterates over the number of nodes in the layer before this layer of weights
         {
            w[n][k] = new double[numNodes[n + 1]];                 // third index of w array is the number of nodes in the layer right of weights

            for (int j = 0; j < numNodes[n + 1]; j++)              // iterates over the number of nodes from the layer after this layer of weights
            {
               w[n][k][j] = Double.parseDouble(readW.readLine());  // parses the next line in weights.txt into a double and stores in w array
               //System.out.println("w[" + n + "][" + k + "][" + j   // prints out weight values with its respective indices for purposes of debug
               //                   + "]: " + w[n][k][j] + " ");
            }
         } // for (int k = 0; k < numNodes[n]; k++)

      } // for (int n = 0; n < numHiddenLayer + 1; n++)
   } // public void setWeights() throws IOException

   /**
    * Fills the instance variable array a with its proper values and returns a 2D
    * array with the sum of dot products between activation node values and weight
    * values for every node.
    *
    * Fills the first layer, the input layer, with instance variable array inputNodes.
    * Iterates through all activation layers in the Perceptron starting from the
    * first hidden layer. Goes through each node of every layer, fills it by computing
    * the dot product sums between the activation node values and weight values, and
    * throws the dot product into the threshold method.
    */
   public void output()
   {
      a[0] = inputNodes;                                   // fills the first layer with inputNodes that has values of all input nodes

      for (int n = 1; n < numTotalLayers; n++)             // iterates over all layers except the first, handled above
      {
         for (int j = 0; j < numNodes[n]; j++)             // iterates over nodes in the layer to the right of the weights layer
         {
            a[n][j] = 0.0;                                 // zeroes temp to ensure accurate computations for every evaluation

            for (int k = 0; k < numNodes[n - 1]; k++)      // iterates over nodes in the layer to the left of the weights layer
               a[n][j] += (a[n - 1][k] * w[n - 1][k][j]);  // dot product = nodes left of weights * weights

            a[n][j] = threshold(a[n][j]);                  // current node is the dot product sum thrown into the threshold method
         } // for (int j = 0; j < numNodes[n]; j++)
      } // for (int n = 1; n < numTotalLayers; n++)
    } // public void output()

   /**
    * Computes the output of param arg by throwing it into a threshold function.
    * The threshold function is sigmoid for this version of the Perceptron.
    *
    * @param arg   the input value that becomes the "x" in the threshold function
    * @return      the output after value is thrown into the threshold function
    */
   public double threshold(double arg)
   {
      return 1.0 / (1.0 + Math.exp(-arg));
   }

   /**
    * Defines the derivative of the threshold function and returns the output
    * of the derivative function at the parameter arg.
    *
    * @param arg   the value at which the derivative function is evaluated
    * @return      the value returned by the derivative function at arg
    */
   public double derivative(double arg)
   {
      double deriv = threshold(arg);  // stores the value of the derivative of threshold
      return deriv * (1.0 - deriv);
   }

   /**
    * Computes and returns the error value, which is calculated by squaring the
    * difference between tValues and the output values in the last layer of array a
    * computed by output method and then dividing the sum of all differences by 2.
    *
    * @param tValue   stores the expected truth values, hence called T values
    * @return         the error value
    */
   public double calcError(double[] tValue)
   {
      double error = 0.0;
      int lastLayer = numTotalLayers - 1;                   // represents the index of the last layer

      for (int i = 0; i < numNodes[lastLayer]; i++)         // loops over every node in the last layer
      {
         double difference = tValue[i] - a[lastLayer][i];   // finds the difference between the truth value and the node
         error += difference * difference;                  // square the difference and add to the error
      }

      return error * 0.5;                                   // multiply the error value by 0.5
   } // public double calcError(double[] tValue)

   /**
    * Initializes the 3D instance variable called w with a proper size, fills it with
    * randomized weights that fall between the range given by the parameters, and writes
    * all the weight values into a file whose name is given by parameter as well.
    *
    * @param low            the lower limit for randomized weights
    * @param high           the higher limit for randomized weights
    * @param file           the name of file where the user wants his or her weights to be stored in
    * @throws IOException   thrown to indicate a failure in Input/Output operations while attempting to read
    */
   public void randomizeW(double low, double high, String file) throws IOException
   {
      BufferedWriter writeW = new BufferedWriter
                              (new FileWriter(file));                // creates a BufferedWriter to write weights into a file
      Random rand = new Random();

      w = new double[numHiddenLayer + 1][0][0];                      // total weights layers is numHiddenLayer + 1 (add the layer from inputs)

      for (int n = 0; n < numHiddenLayer + 1; n++)                   // iterates over all layers of weights
      {
         w[n] = new double[numNodes[n]][0];                          // second index of w is the number of nodes in the layer left of weights

         for (int k = 0; k < numNodes[n]; k++)                       // iterates over the nodes in the layer before this layer of weights
         {
            w[n][k] = new double[numNodes[n + 1]];                   // third index of w is the number of nodes in the layer right of weights

            for (int j = 0; j < numNodes[n + 1]; j++)                // iterates over the nodes in the layer after this layer of weights
            {
               w[n][k][j] = rand.nextDouble() * (high - low) + low;  // weights should fall within the range of param low and high
               writeW.write(String.valueOf(w[n][k][j]) + "\n");      // writes the randomized weight values into a file
            }

         } // for (int k = 0; k < numNodes[n]; k++)
      } // for (int n = 0; n < numHiddenLayer + 1; n++)

      writeW.close();                                                // closes the file to finish writing
   } // public void randomizeW(double low, double high, String file) throws IOException

   /**
    * Randomizes weights given the bounds using the randomizeW method. Implemented
    * with the back propagation algorithm to train and minimize errors. Adjusts the
    * weights in 3D instance variable array w using the gradient descent to find the
    * best set of values that allows all given test sets to reach an error value
    * that is less than or equal to the parameter error threshold given by the user.
    * For more detailed mathematical explanations about the gradient descent, refer
    * to Dr. Nelson's notes in file "4-Three_Plus_Layer_Network."
    *
    * If error threshold is not reached, the method will stop adjusting weights after the
    * number of training iterations exceeds the limit given by the parameter iterations.
    * After the method stops adjusting weights, training is done, so it prints out all
    * final weight values, the truth values and the output nodes side by side, and the
    * error reached for each test set onto the console. The final values of the weights
    * in w would also be written in a file whose name is determined by parameter file
    * given by the user.
    *
    * @param iterations     the maximum number of iterations allowed before stopping training
    * @param file           the file name that user wants the trained weights to be written in
    * @param numTests       the number of test sets given in the inputs.txt
    * @param threshold      specifies what the maximum error acceptable for ending training
    * @param lambda         specifies the learning factor, how far weights move for each step
    * @param low            the lower limit for randomized weights
    * @param high           the higher limit for randomized weights
    * @param input   the file name that user puts his or her input values in
    * @throws IOException   thrown to indicate a failure in Input/Output operations while attempting to read
    */
   public void gradient(int iterations, String file, int numTests, double threshold, double lambda,
                        double low, double high, String input) throws IOException
   {
      randomizeW(low, high, file);                                           // randomize weights to start off

      int current = 0;                                                       // the current number of iterations
      boolean trained = false;                                               // whether error has reached the threshold value

      int lastLayer = numTotalLayers - 1;                                    // the last layer index pointing to the output layer
      int numInputs = numNodes[0];                                           // number of nodes in the input layer
      int numOutputs = numNodes[lastLayer];                                  // number of nodes in the output layer

      double[][] testSets = new double[numTests][numInputs];                 // stores the input layer nodes for each test set
      double[][] t = new double[numTests][numOutputs];                       // stores the truth values for each test set
      double[] error = new double[numTests];                                 // stores the error value for each test set

      double[][] theta = new double[numTotalLayers][];                       // stores the sum of dot products for each layer
      double[][] psi = new double[numTotalLayers][];
      double[][] omega = new double[numTotalLayers][];                       // stores the sum of products of psi and weights for each layer
      for (int n = 0; n < numTotalLayers; n++)                               // iterates over all layers to initialize theta, psi, and omega
      {
         omega[n] = new double[numNodes[n]];                                 // size of omega, psi, and theta for the current index
         psi[n] = new double[numNodes[n]];                                   // is the number of nodes in the current layer
         theta[n] = new double[numNodes[n]];
      }

      BufferedReader readI = new BufferedReader                              // creates a BufferedReader to read inputs from inputs.txt
                             (new FileReader(input));

      String[] line = new String[numInputs + numOutputs];                    // an empty array of Strings that holds inputs and T values
      for (int test = 0; test < numTests; test++)                            // fills testSets and t arrays with inputs and T values
      {
         line = readI.readLine().split(" ");                                 // puts each String that is separated by white spaces in the
                                                                             // current line read by the Reader into the array called line

         for (int k = 0; k < numInputs; k++)                                 // iterates over the number of input nodes in the current line
            testSets[test][k] = Double.parseDouble(line[k]);                 // fills testSets array with given inputs for the current set

         for (int i = 0; i < numOutputs; i++)
            t[test][i] = Double.parseDouble(line[numInputs + i]);            // T values should be everything after the input values
      } // for (int test = 0; test < numTests; test++)

      while (trained != true && current < iterations)                        // trains until error is reached or iterations exceed the limit
      {
         for (int test = 0; test < numTests; test++)                         // iterates over every test set
         {
            inputNodes = testSets[test];
            a[0] = inputNodes;                                               // initializes the input layer with the correct set of inputs
            for (int n = 1; n < numTotalLayers; n++)                         // iterates over all layers except the first, handled above
            {
               for (int j = 0; j < numNodes[n]; j++)                         // iterates over nodes in the layer to the right of the weights
               {
                  theta[n][j] = 0.0;                                         // zeroes theta to ensure accurate computations for every case

                  for (int k = 0; k < numNodes[n - 1]; k++)                  // iterates over nodes in the layer to the left of the weights
                     theta[n][j] += (a[n - 1][k] * w[n - 1][k][j]);          // dot product = nodes left of weights * weights

                  a[n][j] = threshold(theta[n][j]);                          // current node is the dot product sum thrown into threshold method
               } // for (int j = 0; j < numNodes[n]; j++)
            } // for (int n = 1; n < numTotalLayers; n++)

            for (int i = 0; i < numNodes[lastLayer]; i++)                    // iterates over last layer to calculate special "omegas" (only for
               omega[lastLayer][i] = t[test][i] - a[lastLayer][i];           // the last layer) as the differences between T values and outputs

            for (int alpha = lastLayer; alpha > 0; alpha--)                  // alpha represents the index of the current layer
            {
               for (int gamma = 0; gamma < numNodes[alpha - 1]; gamma++)     // gamma represents the index of nodes in the layer to the left
               {
                  omega[alpha - 1][gamma] = 0.0;                             // zeroes omega to avoid overlap between test cases

                  for (int beta = 0; beta < numNodes[alpha]; beta++)         // beta represents the index of nodes in the current layer
                  {
                     psi[alpha][beta] = omega[alpha][beta] *                 // psi stores the product of the current omega value and
                                        derivative(theta[alpha][beta]);      // the derivative function evaluated at current theta value

                     omega[alpha - 1][gamma] += psi[alpha][beta] *           // omega of the layer to the left is the sum of products of psi
                                                w[alpha - 1][gamma][beta];   // and weights connecting to the current layer

                     w[alpha - 1][gamma][beta] += lambda * psi[alpha][beta]  // updates weights since the omegas of the layer to the left
                                                  * a[alpha - 1][gamma];     // has been calculated above
                  } // for (int beta = 0; beta < numNodes[alpha]; beta++)

               } // for (int gamma = 0; gamma < numNodes[alpha - 1]; gamma++)
            } // for (int alpha = lastLayer; alpha > 0; alpha--)

            output();                                                        // fills the array a according to the adjusted weights
            error[test] = calcError(t[test]);                                // calculates the new error value with new values in a
            if (error[test] <= threshold)                                    // if any set satisfies the error threshold
               trained = true;                                               // set to true to check all sets after this loop
         } // for (int test = 0; test < numTests; test++)

         if (trained == true)                                                // if any of the test sets reached the threshold, check all
         {
            for (int test = 0; test < numTests; test++)                      // iterates over all sets to check
            {
               if (Math.abs(error[test]) > threshold)                        // a set is above the threshold means network is not trained
                  trained = false;
            }
         }

         current++;                                                          // increments the number of iterations since one just finished
      } // while (current < iterations && minimized != true)

      System.out.println("------RESULTS BELOW------");                       // everything below is for printing out hyperparameters:
      System.out.println("Maximum number of iterations allowed: " +          // maximum number of iterations allowed by param from user
                         iterations);
      System.out.println("Error threshold: " + threshold);                   // error threshold that should be reached given by param
      System.out.println("Learning factor: " + lambda);                      // the learning factor defined by param lambda
      System.out.println("Weights are randomized in the range from "         // the range for randomization of weights
                         + low + " to " + high);

      BufferedWriter writeW = new BufferedWriter
                              (new FileWriter(file));                        // creates a BufferedWriter to write weights into file

      //BufferedWriter writeO = new BufferedWriter
      //                    (new FileWriter("outputs.txt"));

      System.out.println("Iterations used: " + current);                     // prints out number of iterations used
      for (int n = 0; n < lastLayer; n++)                                    // following for loops iterate over the array w
      {
         for (int k = 0; k < numNodes[n]; k++)
         {
            for (int j = 0; j < numNodes[n + 1]; j++)
            {
               writeW.write(w[n][k][j] + "\n");                              // writes all the weight values after training into file
               //System.out.println("w[" + n +"]["  + k + "][" + j +           // prints out all the weight values after training
               //                   "]: " + w[n][k][j]);
            }
         } // for (int k = 0; k < numNodes[n]; k++)
      } // for (int n = 0; n < lastLayer; n++)

      for (int test = 0; test < numTests; test++)                            // iterates over all sets
      {
         inputNodes = testSets[test];                                        // fills the inputNodes instance field with the current set
         output();                                                           // fills array a with the current set of inputs and weights

         System.out.println("-----For test set #" + (test + 1));             // everything below prints results of current set:
         for (int i = 0; i < numOutputs; i++)                                // iterates over every output node
         {
            //writeO.write(a[lastLayer][i] + " ");
            System.out.print("T value: " + t[test][i] + " ");                // the current truth value
            System.out.println("a[" + lastLayer + "][" + i + "]: "           // the value of the current output node
                                + a[lastLayer][i]);
         }
         System.out.println("Error value: " + error[test]);                  // the error value reached

         for (int n = 0; n < numTotalLayers; n++)                            // iterates over all layers of the Perceptron
            System.out.println("There are " + numNodes[n] +                  // prints out the dimensions of the Perceptron
                               " activations in layer " + (n + 1));
      } // for (int test = 0; test < numTests; test++)

      writeW.close();                                                        // closes the file with the trained weights
      //writeO.close();
   } // public void gradient(int iterations, String file, int numTests, double threshold,
     // double lambda, double low, double high, String input) throws IOException


   /**
    * Trains the Perceptron by initializing 2D instance variable a with setA method
    * and calls the gradient method, which is implemented with the back propagation
    * algorithm, randomizes weights first and then adjusts them using gradient descent.
    *
    * @param iterations     the maximum number of iterations allowed before stopping training
    * @param file           the file name that user wants the trained weights to be written in
    * @param numTests       the number of test sets given in the inputs.txt
    * @param threshold      specifies what the maximum error acceptable for ending training
    * @param lambda         specifies the learning factor, how far weights can move down hill for each step
    * @param low            the lower limit for randomized weights
    * @param high           the higher limit for randomized weights
    * @param input   the file name that user puts his or her input values in
    * @throws IOException   thrown to indicate a failure in Input/Output operations while attempting to read
    */
   public void train(int iterations, String file, int numTests, double threshold, double lambda, double low, double high, String input) throws IOException
   {
      setA();
      gradient(iterations, file, numTests, threshold, lambda, low, high, input);
   }

   /**
    * Starts the testing of the Perceptron by first initializing instance variable
    * array a with setA method and instance variable array w with setWeights method,
    * reads input values and T values from a file named inputs.txt using a BufferedReader,
    * fills the array a with correctly computed values by using the output method, and
    * prints out the calculated error value.
    *
    * @param input   the file name that user puts his or her input values in
    * @throws IOException   thrown to indicate a failure in Input/Output operations while attempting to read
    */
   public void run(String input) throws IOException
   {
      setA();                                                       // initializes instance variable array a
      setWeights();                                                 // initializes instance variable array w

      BufferedReader readI = new BufferedReader
                             (new FileReader(input));               // creates a BufferedReader to read inputs from inputs.txt

      String curr = readI.readLine();                               // reads the first line in inputs.txt: inputs and respective T values

      int lastLayer = numTotalLayers - 1;                           // index of the last layer
      int numOutputs = numNodes[lastLayer];                         // the number of output nodes
      int numInputs = numNodes[0];                                  // the number of input nodes

      String[] line = new String[numInputs + numOutputs];           // creates an empty array of Strings to hold the input values and T value

      while (curr != null)                                          // iterates over every line in inputs.txt until it hits EOF
      {
         line = curr.split(" ");                                    // split method puts each String separated by white spaces into an array

         for (int k = 0; k < numInputs; k++)                        // iterates over the number of input nodes to initialize inputNodes
            inputNodes[k] = Double.parseDouble(line[k]);            // fills instance variable array inputNodes with the input values

         double[] tValue = new double[numOutputs];                  // the number of T values should equal to the number of output nodes
         for (int i = 0; i < numOutputs; i++)
            tValue[i] = Double.parseDouble(line[numInputs + i]);    // T values should be everything in the line after the input values

         output();                                                  // calls the output method to fill array a now that inputNodes is filled

         for (int i = 0; i < numOutputs; i++)                       // iterates over every output node to print out each value
         {
            System.out.print("T value: " + tValue[i] + " ");
            System.out.println("a[" + lastLayer + "][" + i + "]: "
                               + a[lastLayer][i]);
         }
         System.out.println("Error value: " + calcError(tValue));   // prints the calculated error value using the calcError method

         curr = readI.readLine();                                   // reads the next line in inputs.txt
         System.out.println();                                      // the next set of inputs and results will start printing on a new line
      } // while (curr != null)

   } // public void run(String input) throws IOException

   /**
    * Reads the dimensions of the Perceptron with a BufferedReader and a file called dimensions.txt,
    * uses those information to create a Perceptron object, asks user input from the Console to
    * either run or train the Perceptron and do it accordingly.
    *
    * To train, the user need to put all their relevant training hyperparameters in a file
    * organized as follows (one on each line): the maximum number of iterations, the file name
    * to store the weights in it after training, the number of given test cases, the target
    * error threshold, the learning factor, the lower limit for weights randomization,
    * and the upper limit for weights randomization.
    *
    * @param args           specify the program to be tested
    * @throws IOException   thrown to indicate a failure in Input/Output operations while attempting to read
    */
   public static void main(String[] args) throws IOException
   {
      BufferedReader br = new BufferedReader
                          (new FileReader("dimensions.txt"));              // creates a BufferedReader to read dimensions from dimensions.txt

      int inputNodes = Integer.parseInt(br.readLine());                    // first line in the file is number of nodes in input layer

      int hiddenLayer = Integer.parseInt(br.readLine());                   // second line if the file is number of hidden layers
      int[] hiddenNode = new int[hiddenLayer];                             // creates a 1D array with the size as the number of hidden layers
      for (int n = 1; n < hiddenLayer + 1; n++)                            // iterates over every hidden layer there is
         hiddenNode[n - 1] = Integer.parseInt(br.readLine());              // following lines in the file are number of nodes in each hidden layer

      int outputN = Integer.parseInt(br.readLine());                       // last line in the file is the number of nodes in the output layer

      Perceptron test = new Perceptron(inputNodes, hiddenNode, outputN);   // creates a Perceptron object

      Scanner sc = new Scanner(System.in);                                 // creates a Scanner to take input directly from the console
      System.out.print("File name that has your input values: ");
      String input = sc.next();
      System.out.print("Do you want to run or train (r/t): ");             // asks the user to train or run the network
      String next = sc.next().toLowerCase();                               // converts user input to lowercase to simplify the conditionals

      if (next.equals("r") || next.equals("run"))                          // runs the Perceptron by calling run method
         test.run(input);
      else if (next.equals("t") || next.equals("train"))                   // trains the Perceptron by asking more input to call train method
      {
         System.out.print("Please provide the name of the file that " +    // asks for the file user has with all training hyperparameters
                          "contains your hyperparameters: ");
         BufferedReader tr = new BufferedReader
                             (new FileReader(sc.next()));                  // creates a BufferedReader to read user's given file for training

         int iterations = Integer.parseInt(tr.readLine());                 // first line in the file is the number of iterations allowed
         String file = tr.readLine();                                      // second line in the file is the file name to store weights in
         int tests = Integer.parseInt(tr.readLine());                      // third line in the file is the number of test cases provided
         double error = Double.parseDouble(tr.readLine());                 // fourth line in the file is the target error threshold
         double lambda = Double.parseDouble(tr.readLine());                // fifth line in the file is the learning factor
         double low = Double.parseDouble(tr.readLine());                   // sixth line in the file is the lower limit for randomization of w
         double high = Double.parseDouble(tr.readLine());                  // seventh line in the file is the upper limit for randomization of w

         long startTime = System.currentTimeMillis();                             // records the start time of training
         test.train(iterations, file, tests, error, lambda, low, high, input);    // trains the Perceptron by calling the train method
         long endTime = System.currentTimeMillis();                               // records the end time of training
         System.out.println("Training took " + (endTime - startTime) +            // prints out how long training took in milliseconds
                          " milliseconds");
      }
   } // public static void main(String[] args) throws IOException
} // public class Perceptron
