package ryanairScanner;

public class CircularQueue extends Queue
{
  private Object[] slots;
  private int first, last, num;
  private final int SIZE;
    
  public CircularQueue(int s)
  {
    slots = new Object[s];
    SIZE = s;
    first = last = num = 0;
  }

  // Number of elements
  public int Rank()
  {
    return num;
  }
    
  // Number of free slots available
  public int Free()
  {
    return SIZE - num;
  }
    
  // Is full ?
  public boolean IsFull()
  {
    return num == SIZE;
  }

  // Put value at queue's tail
  public void Put(Object value)
  {
    slots[last] = value;
    last = (last + 1) % SIZE;
    num++;
  }

  // Get value from queue's head
  public Object Get()
  {
    Object result = slots[first];
    first = (first + 1) % SIZE;
    num--;
    return result;
  }
}
