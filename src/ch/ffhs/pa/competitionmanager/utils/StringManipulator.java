package ch.ffhs.pa.competitionmanager.utils;

public class StringManipulator {

    // https://stackoverflow.com/questions/32498432/add-escape-in-front-of-special-character-for-a-string
    public static String escapeMetaCharacters(String inputString){
        final String[] metaCharacters = {"\\","^","$","{","}","[","]","(",")",".","*","+","?","|","<",">","-","&","%"};

        for (int i = 0 ; i < metaCharacters.length ; i++){
            if(inputString.contains(metaCharacters[i])){
                inputString = inputString.replace(metaCharacters[i],"\\" + metaCharacters[i]);
            }
        }
        return inputString;
    }
}
