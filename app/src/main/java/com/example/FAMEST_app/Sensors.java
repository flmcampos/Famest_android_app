package com.example.FAMEST_app;

public class Sensors {

    private interface StringMaker{
        String toSString();
    }
    public class Flexiforce implements StringMaker{
        int hal, met1, met2, met3, mid, cal1, cal2;
        int[] flx = new int[7];

        @Override
        public String toSString() {
            hal=flx[0];
            met1=flx[1];
            met2=flx[2];
            met3=flx[3];
            mid=flx[4];
            cal1=flx[5];
            cal2=flx[6];

            return hal+","+met1+","+met2+","+met3+","+mid+","+cal1+","+cal2;
        }
    }

    public class AM2320 implements StringMaker {
        float Temp;
        float Hum;
        float[] am2320 = new float[]{Temp, Hum};

        @Override
        public String toSString() {
            Temp=am2320[0];
            Hum=am2320[1];
            return Temp+","+Hum;
        }
    }
}
