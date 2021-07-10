package tk.smashr.smashit;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

class SmashingLogic {
    static Integer namingMethod;
    static String baseName;
    static Integer numberOfKahoots;
    static Integer smashingMode;
    //Smashing logic
    private static String namesExample[] = {
        "Ben_Dover", "Eileen_Dover", "Mike_Hawk", "Hugh_Jass", "Chris_Peacock", "Heywood_Jablome", "Dixie_Normous", 
        "Barry_McKockiner", "Duncan_McOkiner", "Hugh_G_Rection", "Mike_Oxlong", "Phil_McCraken", "Ifarr_Tallnight",
        "Gabe_Itch", "Moe_Lester", "Justin_Herass", "Todd_Lerfondler", "Gabe_Utsecks", "Stan_Keepus", "Tara_Dikoff",
        "Eric_Shawn", "Alpha_Q", "Hugh_Janus", "Mike_Rotch_Burns", "Pat_Myza", "Betty_Phucker", "Knee_Grow", "Ms_Carriage",
        "Ray_Pist", "Harry_Anoos", "Maya_Normus_Bhut", "Dang_Lin_Wang", "Anna_Borshin", "Hari_Balsac", "Ped_O'Phyl",
        "Wilma_Dikfit", "School_Kahooter", "Tera_Wrist", "York_Oxmall", "Craven_Morehed", "Ice_Wallow_Come", "Jyant_Deck",
        "Willie_B_Hardigan", "E_Norma_Stits", "Anita_P_Ness", "Bo_Nerr", "Gray_Zerclit", "Mike_Hunt", "Jack_Meoff", 
        "Jack_Goff", "Jenny_Talia", "Mike_Lit", "Tess_Tickles", "Philip_Macroch", "Duncan_McCoconah", "Anne_Null",
        "Cam_L_Toe", "Matt_Sterbater", "Harry_Coccen_Mihan", "Zuch_Mabaulz", "Baul_Zack", "Cle_Torres", "Taj_Maddick",
        "Pooh_See", "Dig_Bick", "Dill_Dough"
       // "Mohxy", "Vxire", "Gahmuret", "Tara", "Tomi", "Argor", "Kelretu", "Polyzynn", "Tekrala", "Kesta", "Suelle",
       // "cuntlad", "BuyMyAids", "Wilihey", "Milkshook", "Kanker Luke", "SUCK YOUR MUM", "Acer", "ChRoMoSoMe CoLlEcToR",
    }

    private static String randomCaps(String baseName) {
        StringBuilder newName = new StringBuilder();
        for (int i = 0; i < baseName.length(); i++) {
            if (Math.random() > 0.5) {
                newName.append(String.valueOf(baseName.charAt(i)).toUpperCase());
            } else {
                newName.append(String.valueOf(baseName.charAt(i)).toLowerCase());
            }
        }
        return newName.toString();
    }

    private static String generateRandomLetter(int length) {
        StringBuilder randomLetters = new StringBuilder();
        String letters = "qwertyuiopasdfghjklzxcvbnm1234567890";
        for (int i = 0; i < length; i++) {
            randomLetters.append(letters.charAt((int) (Math.random() * letters.length())));
        }
        return randomLetters.toString();
    }

    static String generateName(int id) {
        String name;
        switch (namingMethod) {
            case 0:
                name = randomCaps(namesExample[(int) (Math.random() * namesExample.length)]);
                if (name.length() > 15) {
                    name = name.substring(0, 15);
                }
                break;
            case 1:

                name = (baseName + '.' + generateRandomLetter(15)).substring(0, 15);
                break;
            case 2:
                if (baseName.length() < 7) {
                    name = randomCaps(baseName) + '.' + generateRandomLetter(4);
                    break;
                } else {
                    if (randomCaps(baseName).length() > 15) {
                        name = randomCaps(baseName).substring(0, 15);
                    } else {
                        name = randomCaps(baseName);
                    }
                    break;
                }
            case 3:
                name = baseName + id;
                if (name.length() > 15) {
                    name = id + "";
                }
                break;
            default:
                name = "Smasher" + generateRandomLetter(5);
        }
        return name;
    }

    static void saveToFile(Context context) {
        File path = context.getFilesDir();
        File file = new File(path, "settings.csv");

        String fileContent = numberOfKahoots.toString() + "," + namingMethod.toString() + "," + baseName + "," + smashingMode;

        try {
            FileOutputStream stream = new FileOutputStream(file);
            stream.write(fileContent.getBytes());
            stream.close();
        } catch (FileNotFoundException e) {
            Log.println(Log.ERROR, "File system", "Unknown save error (FileNotFound)");
        } catch (IOException e) {
            Log.println(Log.ERROR, "File system", "Unknown save error(IO)");
        }
    }


    static void readAndInterpretSettings(Context context) {
        try {
            String toRead = readSettings(context);
            String values[] = toRead.split(",");
            numberOfKahoots = Integer.parseInt(values[0]);
            namingMethod = Integer.parseInt(values[1]);
            baseName = values[2];
            smashingMode = Integer.parseInt(values[3]);
        } catch (Exception e) {
            Log.println(Log.ERROR, "Bad File", "File is corrupt, rectifying issue");
            numberOfKahoots = 75;
            namingMethod = 0;
            baseName = "smasher";
            smashingMode = 0;

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
            try (FileInputStream inStream = new FileInputStream(file)) {
                inStream.read(bytes);
                contents = new String(bytes);

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
