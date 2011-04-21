#define ANALOG0 14
#define AVERAGE_SPREAD 50

class Accelerometer
{
    int p[3]; // which analog pins
    int a[3]; // acceleration, zero-based
    int b[3]; // acceleration bias/calibration information
    int g, t, r; // cached copies of calculations
    int average[3]; // average values of the axis readings
    int averageCounter;
    int scale; // scaling factor between ADC and gravity

public:
    Accelerometer(int pinX, int pinY, int pinZ)
    {
        averageCounter = 0;
      
        pinMode((p[0] = pinX) + ANALOG0, INPUT);
        pinMode((p[1] = pinY) + ANALOG0, INPUT);
        pinMode((p[2] = pinZ) + ANALOG0, INPUT);
            
        g = t = r = 0;
        scale = 100;
        
//        b[0] = 392;
//        b[1] = 382;
//        b[2] = 546 - scale;

        b[0] = 0;
        b[1] = 0;
        b[2] = 0;
    }

    void update()
    {
        for (int i = 0; i < 3; i++)
        {
          int measuredValue = analogRead(p[i]) - b[i];
          average[i] += measuredValue;
          
          averageCounter++;
          
          if(averageCounter >= AVERAGE_SPREAD)
          {
            a[i] = (average[i] / AVERAGE_SPREAD);
            
            averageCounter = 0;
            average[i] = 0;
          }
        }
            
        g = t = r = 0;
    }

    void dump()
    {
        Serial.print( "x="); Serial.print((a[0] * 0.529411) - 180);
        Serial.print(" y="); Serial.print((a[1] * 0.529411) - 180);
        Serial.print(" z="); Serial.print((a[2] * 0.529411) - 180);
//        Serial.print(" pitch="); Serial.print(pitch());
//        Serial.print(" roll="); Serial.print(roll());
        Serial.println();
    }

    void calibrate()
    {
        for (int i = 0; i < 3; i++)
            b[i] = analogRead(p[i]);
            
        b[2] -= scale;
        
        Serial.print( "b1="); Serial.print(b[0]);
        Serial.print(" b2="); Serial.print(b[1]);
        Serial.print(" b3="); Serial.print(b[2]);
        
        update();
    }

    int milligee()
    {
        if (g != 0) return g;
        long squared = 0.0;
        for (int i = 0; i < 3; i++)
            squared += (long)a[i] * (long)a[i];
        g = squared * 1000 / (scale*scale);
        return g;
    }

    int accel(int axis)
    {
        if (axis < 0 || axis > 3) return 0;
        return a[axis];
    }

    int roll()
    {
        if (r != 0) return r;
        r = (int)(atan2(a[0], a[2]) * 180. / M_PI);
        return r;
    }

    int pitch()
    {
        if (t != 0) return t;
        t = (int)(acos(a[1] / (float)scale) * 180. / M_PI);
        return t;
    }

    void loop()
    {
        update();
    }
};








void setup()
{
    Serial.begin(9600);
    
    int anger = 0;
    Accelerometer accel = Accelerometer(0, 1, 2);
    
    delay(20);
    accel.loop();

    pinMode(8, INPUT); digitalWrite(8, HIGH); // internal pullup

    int div = 0;
    while (1)
    {
        delay(20);
        accel.loop();
        
        if (LOW == digitalRead(8))
            accel.calibrate();
            
        if (--div <= 0)
        {
          div = 25;
          accel.dump();
        }
    }
}

void loop() { ; } // we do our own loop below
