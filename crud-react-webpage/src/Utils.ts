/**
 * It clones the object and returns the clone
 * @param something 
 * @returns clone of something
 */
export function clone(something:any):any{
    return JSON.parse(JSON.stringify(something));
}

/**
 * Date to String converter and formater
 * Use to avoid fails in snapshots when testing with jest
 */
export abstract class MyDateToString{
    constructor(){}
    /**
     * Converts to string and formats it
     * @param date to convert
     * @returns string of the date, formated
     */
    static dateToString(date:Date):string{
        if(date==undefined)
            return '';
        return ddMMyyyy(date);
        //return date.toString();
    }
    static dateToStringP(date:Date):Promise<string>{
        return new Promise(function(resolve,reject){
            resolve(date.toString());
        });
    }
}

/**
 * Make bigString shorter taking 4 last chars
 * @param bigString to cut
 * @returns 
 */
export function fourLastChars(bigString:string):string{
    return bigString.substr(bigString.length-4,4);
}

/**
 * Converts to string and formats it with pattern dd-mm-yyy
 * @param date to convert
 * @returns string of the date, formated
 */
function ddMMyyyy(date:Date):string{
    if(date instanceof Date && date!=undefined && date!=null && !isNaN(date.getTime()))
        return date.getDay().toString()+'-'+date.getMonth().toString()+'-'+date.getFullYear().toString();
    else
        return '';
}