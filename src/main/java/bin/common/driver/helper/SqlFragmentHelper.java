package bin.common.driver.helper;

/**
 * SQL��Ƭ������
 */
public class SqlFragmentHelper {

    /**
     * 'WHERE' ���ַ�����
     */
    private char[] whereChars=new char[]{'W','H','E','R','E'};
    /**
     * 'AND'���ַ�����
     */
    private char[] andChars=new char[] {'A','N','D'};
    /**
     * 'OR'���ַ�����
     */
    private char[] orChars=new char[]{'O','R'};
    /**
     * 'WHERE'�ĳ���
     */
    private int wherelength=whereChars.length;
    /**
     * 'AND'�ĳ���
     */
    private int andlength=andChars.length;
    /**
     * 'OR'�ĳ���
     */
    private int orlength=orChars.length;
    /**
     * 'WHERE'�����һ���ַ�
     */
    private char whereLastChar=whereChars[wherelength-1];
    /**
     * 'AND'�����һ���ַ�
     */
    private char andLastChar=andChars[andlength-1];
    /**
     * 'OR'�����һ���ַ�
     */
    private char orLastChar=orChars[orlength-1];
    /**
     * 'AND'�ĵ�һ���ַ�
     */
    private char andFirstChar=andChars[0];
    /**
     * 'OR'�ĵ�һ���ַ�
     */
    private char orFristChar=orChars[0];

    /**
     *  {@code sqlFrag} �Ƿ���'WHERE'��β
     * @param sqlFrag SQL��Ƭ
     * @param endOffset ����Ϊֹ��ƫ��
     * @return ��'WHERE'��βʱ����true�����򷵻�false
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
     * ���� {@code sqlFrag} ��ǰ׺�����ǰ׺��'AND'����'OR'����Խ���ȥ��
     * @param sqlFrag SQL��Ƭ
     * @return �����õ�SQL��Ƭ
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
     * ���� {@code sqlFrag} �ĺ�׺����� {@code sqlFrag} ����'WHERE','AND','OR' ��ͷ�ģ����ᱻ����ȥ��
     * @param sqlFrag SQL��Ƭ
     * @return �����õ�SQL��Ƭ
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
     * ���ж� {@code sqlFragChar} �Ƿ��� {@code keyArr} ��β
     * @param i {@code sqlFragChars} ������λ��
     * @param keyArr ���Ŀ��
     * @param sqlFragChars ��������
     * @return �����{@code sqlFragChar} ���� {@code keyArr} ��β����true�����򷵻�false
     */
    private boolean isKeyArrfromTailStart(int i, char[] keyArr, char[] sqlFragChars){
        int keyLength=keyArr.length;
        int j;
        int cursor=i-1;
        for ( j = keyLength>=2?keyLength-2:0; j >= 0; j--) {//�ӵ����ڶ����ַ���ʼ��飬��Ϊ���÷���֮ǰ������һ���ַ��Ѿ���������
            if(sqlFragChars[cursor]!=keyArr[j]){
                break;
            }
            cursor--;
        }
        return j==-1;
    }

    /**
     * ���ж� {@code sqlFragChar} �Ƿ��� {@code keyArr} ��ͷ
     * @param i {@code sqlFragChars} ������λ��
     * @param keyArr ���Ŀ��
     * @param sqlFragChars ��������
     * @return �����{@code sqlFragChar} ���� {@code keyArr} ��β����true�����򷵻�false
     */
    private boolean isKeyArrfromHeadStart(int i, char[] keyArr, char[] sqlFragChars){
        int keyLength=keyArr.length;
        int j;
        int cursor=i+1;
        for ( j = 1; j < keyLength; j++) {//�ӵڶ�����ʼ��飬��Ϊ���÷���֮ǰ��һ���ַ��Ѿ���������
            if(sqlFragChars[cursor]!=keyArr[j]){
                break;
            }
            cursor++;
        }
        return j==keyLength;
    }


}
