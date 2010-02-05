package ryanairScanner;

public class Consumer extends Thread
{
  protected Buffer b;
  private int id;

  public Consumer(Buffer b, int id)
  {
    this.b = b;
    this.id = id;
  }

  public void run()
  {
    Object value;
        
    
    
    while (true)
      {
        System.out.println("      -> Consumer #" + id + ": [ get ...");
        value = b.Get();
        System.out.println("      -> Consumer #" + id + ": " + value.toString() + " ]");
        consume(value);
      }
  }

  private void consume(Object value)
  {
    try
      {
        sleep((int)(Math.random() * 100));
      }
    catch (InterruptedException e)
      {
      }
  }
}
