package rc.loveq.eye.data.model;

import java.util.List;

/**
 * Author：Rc
 * 0n 2018/1/27 21:41
 */

public class Daily {
    public String nextPageUrl;
    public List<IssueList> issueList;

    public static class IssueList {
        public long releaseTime;
        public String type;
        public long date;
        public long publishTime;
        public int count;
        public List<ItemList> itemList;
    }
}
