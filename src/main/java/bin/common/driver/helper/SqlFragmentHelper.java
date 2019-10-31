package bin.common.driver.helper;

/**
 * SQL碎片帮助类
 */
public class SqlFragmentHelper {

    /**
     * 'WHERE' 的字符数组
     */
    private char[] whereChars=new char[]{'W','H','E','R','E'};
    /**
     * 'AND'的字符数组
     */
    private char[] andChars=new char[] {'A','N','D'};
    /**
     * 'OR'的字符数组
     */
    private char[] orChars=new char[]{'O','R'};
    /**
     * 'WHERE'的长度
     */
    private int wherelength=whereChars.length;
    /**
     * 'AND'的长度
     */
    private int andlength=andChars.length;
    /**
     * 'OR'的长度
     */
    private int orlength=orChars.length;
    /**
     * 'WHERE'的最后一个字符
     */
    private char whereLastChar=whereChars[wherelength-1];
    /**
     * 'AND'的最后一个字符
     */
    private char andLastChar=andChars[andlength-1];
    /**
     * 'OR'的最后一个字符
     */
    private char orLastChar=orChars[orlength-1];
    /**
     * 'AND'的第一个字符
     */
    private char andFirstChar=andChars[0];
    /**
     * 'OR'的第一个字符
     */
    private char orFristChar=orChars[0];

    /**
     *  {@code sqlFrag} 是否以'WHERE'结尾
     * @param sqlFrag SQL碎片
     * @param endOffset 结束为止的偏移
     * @return 是'WHERE'结尾时返回true；否则返回false
     */
    public boolean isWhereAsTail(String sqlFrag,int endOffset){
        char[] whereSQLArr=sqlFrag.toUpperCase().toCharArray();
        int whereEndInex=-1;
        if(endOffset-1<=0){
            return false;
        }
        for (int i = endOffset-1; i >=0; i--) {
            char c=whereSQLArr[i];
            if(c==' '){
                continue;
            }
            if(c!=whereLastChar){
                break;
            }
            if(c==whereLastChar){
                boolean isWhere=isKeyArrfromTailStart(i,whereChars,whereSQLArr);
                if(isWhere){
                    whereEndInex=i-(wherelength-1);
                }
            }
        }
        return whereEndInex!=-1;
    }

    /**
     * 调整 {@code sqlFrag} 的前缀，如果前缀以'AND'或者'OR'，会对将其去除
     * @param sqlFrag SQL碎片
     * @return 调整好的SQL碎片
     */
    public String correctSqlFragPrefix(String sqlFrag){
        char[] resovledSqlFragArr=sqlFrag.toUpperCase().toCharArray();
        int subStartIndex=-1;
        for (int i = 0; i < resovledSqlFragArr.length; i++) {
            char c=resovledSqlFragArr[i];
            if(c==' '){
                continue;
            }
            if(c!=andFirstChar&&c!=orFristChar){
                break;
            }
            if(c==andFirstChar){
                boolean isAnd=isKeyArrfromHeadStart(i,andChars,resovledSqlFragArr);
                if(isAnd){
                    subStartIndex=i+(andlength);
                    break;
                }
            }
            if(c==orFristChar){
                boolean isOr=isKeyArrfromHeadStart(i,orChars,resovledSqlFragArr);
                if(isOr){
                    subStartIndex=i+(orlength);
                    break;
                }
            }
        }
        if(subStartIndex!=-1){
            return sqlFrag.substring(subStartIndex);
        }
        return sqlFrag;
    }

    /**
     * 调整 {@code sqlFrag} 的后缀，如果 {@code sqlFrag} 是以'WHERE','AND','OR' 开头的，都会被将其去除
     * @param sqlFrag SQL碎片
     * @return 调整好的SQL碎片
     */
    public String correctSqlFragSuffix(String sqlFrag){
        String temp=sqlFrag.toUpperCase();
        int length=sqlFrag.length();
        char[] sqlFragChars=temp.toCharArray();
        int subEndIndex=-1;
        for (int i = length-1; i >=0; i--) {
            char c=sqlFragChars[i];
            if(c==' '){
                continue;
            }
            if(c!=whereLastChar&&c!=andLastChar&&c!=orLastChar){
                break;
            }
            if(c==whereLastChar){
                boolean isWhere=isKeyArrfromTailStart(i,whereChars,sqlFragChars);
                if(isWhere){
                    subEndIndex=i-(wherelength-1);
                    break;
                }
            }
            if(c==orLastChar){
                boolean isOr=isKeyArrfromTailStart(i,orChars,sqlFragChars);
                if(isOr){
                    subEndIndex=i-(orlength-1);
                    break;
                }
            }
            if(c==andLastChar){
                boolean isAnd=isKeyArrfromTailStart(i,andChars,sqlFragChars);
                if(isAnd){
                    subEndIndex=i-(andlength-1);
                    break;
                }
            }
        }
        if(subEndIndex!=-1){
            return sqlFrag.substring(0,subEndIndex);
        }else{
            return sqlFrag;
        }
    }

    /**
     * 从判断 {@code sqlFragChar} 是否以 {@code keyArr} 结尾
     * @param i {@code sqlFragChars} 的索引位置
     * @param keyArr 检查目标
     * @param sqlFragChars 被检查对象
     * @return 如果是{@code sqlFragChar} 是以 {@code keyArr} 结尾返回true；否则返回false
     */
    private boolean isKeyArrfromTailStart(int i, char[] keyArr, char[] sqlFragChars){
        int keyLength=keyArr.length;
        int j;
        int cursor=i-1;
        for ( j = keyLength>=2?keyLength-2:0; j >= 0; j--) {//从倒数第二个字符开始检查，因为调用方法之前倒数第一个字符已经被检查过了
            if(sqlFragChars[cursor]!=keyArr[j]){
                break;
            }
            cursor--;
        }
        return j==-1;
    }

    /**
     * 从判断 {@code sqlFragChar} 是否以 {@code keyArr} 开头
     * @param i {@code sqlFragChars} 的索引位置
     * @param keyArr 检查目标
     * @param sqlFragChars 被检查对象
     * @return 如果是{@code sqlFragChar} 是以 {@code keyArr} 结尾返回true；否则返回false
     */
    private boolean isKeyArrfromHeadStart(int i, char[] keyArr, char[] sqlFragChars){
        int keyLength=keyArr.length;
        int j;
        int cursor=i+1;
        for ( j = 1; j < keyLength; j++) {//从第二个开始检查，因为调用方法之前第一个字符已经被检查过了
            if(sqlFragChars[cursor]!=keyArr[j]){
                break;
            }
            cursor++;
        }
        return j==keyLength;
    }


}
