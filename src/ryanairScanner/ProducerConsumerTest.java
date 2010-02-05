package ryanairScanner;

public class ProducerConsumerTest
{
	//final static int SIZE = 10, NP = 2, NC = 2;
  final static int SIZE = 10, NP = 1, NC = 10;
    
  public static void main(String[] args)
  {
    Buffer b = new Buffer(new CircularQueue(SIZE));
        
    // Create producers
    for (int i=0; i < NP; i++)
      new Producer(b, i).start();

    // Create consumers
    for (int i=0; i < NC; i++)
      new Consumer(b, i).start();
  }
}
