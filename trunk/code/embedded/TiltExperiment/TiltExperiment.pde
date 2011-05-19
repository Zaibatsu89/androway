#define AVERAGE_SPREAD 50

class Accelerometer
{
  int _pins[3];
  int _averageTotal[3];
  int _values[3];  // Average values of the axis readings
  int _averageCounter;

  public:
    Accelerometer(int xPin, int yPin, int zPin)
    {
      _averageCounter = 0;
      _pins[0] = xPin;
      _pins[1] = yPin;
      _pins[2] = zPin;
    }

    void update()
    {
      // Read the sensor values and do some scale transformation
      int xValue = 180 - map(analogRead(_pins[0]), 170, 525, 0, 180);
      int yValue = map(analogRead(_pins[1]), 170, 525, 0, 180) - 90;
      int zValue = map(analogRead(_pins[2]), 170, 525, 0, 180);
      
      // Calculate the correct x value
      if(zValue > 90)
        xValue *= -1;
        
      menageAverage(0, xValue);
      menageAverage(1, yValue);
      menageAverage(2, zValue);
      
      _averageCounter ++;
    }
    
    void menageAverage(int index, int value)
    {
      _averageTotal[index] += value;
          
      if(_averageCounter >= AVERAGE_SPREAD)
      {
        _values[index] = (_averageTotal[index] / AVERAGE_SPREAD);
        _averageTotal[index] = 0;
        _averageCounter = 0;
      }
    }

    void dump()
    {
      Serial.print( "x="); Serial.print(_values[0]);
      Serial.print(" y="); Serial.print(_values[1]);
      Serial.print(" z="); Serial.print(_values[2]);
        
      Serial.println();
    }
    
    void loop()
    {
      update();
    }
};



Accelerometer accel = Accelerometer(0, 1, 2);

void setup()
{
  Serial.begin(9600);
}

long dumpCount = 0;

void loop()
{
  accel.loop();
  
  if (dumpCount >= (AVERAGE_SPREAD * 10))
  {
    dumpCount = 0;
    accel.dump();
  }
  
  dumpCount ++;
}
