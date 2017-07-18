package tk.smashr.smashit;

import android.content.Context;
import android.content.Intent;
import android.os.Debug;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

class SmashingLogic {
    //Smashing logic
    private static String namesExample[]={"Ben Dover","Eileen Dover","Not in ur class","Stephanie","Sportacus","Robbie Rotten","Ziggy","L0kesh;)","RealPerson.mp4","ur search history","Cael Cooper:)","Kim-Jong Uno","Sernie Banders","lorcant","Not A Bot","setup.exe","admin1","Mack attack","mr moo moo man","boris","abdothepedo","pacothetaco","orman","herobine","chuck joris","nerd3","watergaminghd","marijona","SmashKahoot","Kahoot smasher"};
    static Integer numberOfKahoots;
    public  static Integer namingMethod;
    public  static String baseName;
    static Integer smashingMode;

    private static String randomCaps(String baseName)
    {
        String newName = "";
        for(int i=0; i< baseName.length(); i++)
        {
            if(Math.random()>0.5)
            {
                newName+=String.valueOf(baseName.charAt(i)).toUpperCase();
            }
            else
            {
                newName+=String.valueOf(baseName.charAt(i)).toLowerCase();
            }
        }
        return newName;
    }

    private static String generateRandomLetter(int length)
    {
        String randomLetters = "";
        String letters= "qwertyuiopasdfghjklzxcvbnm1234567890";
        for(int i=0; i<length; i++)
        {
            randomLetters += letters.charAt((int)(Math.random()*letters.length()));
        }
        return randomLetters;
    }

    static String generateName(int id)
    {
        String name;
        switch(namingMethod){
            case 0:
                name = randomCaps(namesExample[(int)(Math.random()*namesExample.length)]);
                if(name.length()>15) {
                    name=name.substring(0,15);
                }
                break;
            case 1:

                name = (baseName +'.' +generateRandomLetter(15)).substring(0,15);
                break;
            case 2:
                if(baseName.length()<7)
                {
                    name = randomCaps(baseName) + '.' +generateRandomLetter(4);
                    break;
                }
                else{
                    if(randomCaps(baseName).length()>15) {
                        name = randomCaps(baseName).substring(0, 15);
                    }
                    else
                    {
                        name = randomCaps(baseName);
                    }
                    break;
                }
            case 3:
                name = baseName+id;
                if(name.length()>15)
                {
                    name = id+"";
                }
                break;
            default:
                name = "Smasher"+generateRandomLetter(5);
        }
        return name;
    }
    static void saveToFile(Context context)
    {
        File path = context.getFilesDir();
        File file = new File(path, "settings.csv");

        String fileContent =numberOfKahoots.toString()+","+namingMethod.toString()+","+baseName+","+smashingMode;

        try {
            FileOutputStream stream = new FileOutputStream(file);
            stream.write(fileContent.getBytes());
            stream.close();
        }
        catch (FileNotFoundException e)
        {
            Log.println(Log.ERROR,"File system","Unknown save error (FileNotFound)");
        }
        catch (IOException e)
        {
            Log.println(Log.ERROR,"File system","Unknown save error(IO)");
        }
    }


    static void readAndInterpretSettings(Context context)
    {
        try {
            String toRead = readSettings(context);
            String values[] = toRead.split(",");
            numberOfKahoots = Integer.parseInt(values[0]);
            namingMethod = Integer.parseInt(values[1]);
            baseName = values[2];
            smashingMode = Integer.parseInt(values[3]);
        }
        catch(Exception e)
        {
            Log.println(Log.ERROR,"Bad File","File is corrupt, rectifying issue");
            numberOfKahoots = 75;
            namingMethod = 0;
            baseName = "smasher";
            smashingMode=0;

            saveToFile(context);
        }
    }

    private static String readSettings(Context context) {
        String contents = "";
        File path = context.getFilesDir();
        File file = new File(path, "settings.csv");
        int length = (int) file.length();
        byte[] bytes = new byte[length];
        try {
            FileInputStream inStream = new FileInputStream(file);
            try {
                inStream.read(bytes);
                contents = new String(bytes);

            } finally {
                inStream.close();
            }
        } catch (FileNotFoundException e) {
            String defaultValues = "75,0,smasher,0";
            try {
                FileOutputStream stream = new FileOutputStream(file);
                stream.write(defaultValues.getBytes());
                stream.close();
            } catch (IOException e1) {
                Log.println(Log.DEBUG, "File System", "File not found");
                return "error";
            }
        } catch (IOException e) {
            Log.println(Log.ERROR, "File System", "Critical error");
            return "error";
        }
        return contents;
    }
}
