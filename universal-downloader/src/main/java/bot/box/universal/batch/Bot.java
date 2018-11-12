package bot.box.universal.batch;

import bot.box.universal.callback.BotCallBack;
import bot.box.universal.delegate.BotExecutor;

/**
 * Created by Barry Allen .
 * boxforbot@gmail.com
 */

public class Bot {
    private String url, mPath, mName;
    private BotCallBack mCallback;

    private Bot(InstaBot bot) {
        this.url = bot.url;
        this.mPath = bot.mPath;
        this.mName = bot.mName;
        this.mCallback = bot.mCallback;

        fetchMedia();
    }

    private void fetchMedia() {
        new BotExecutor(this.mPath, this.mName,mCallback).execute(this.url);
    }

    public static class InstaBot {
        private String url, mPath, mName;
        private BotCallBack mCallback;

        public InstaBot feedUrl(String url) {
            this.url = url;
            return this;
        }

        public InstaBot storageDirectory(String dirPath) {
            this.mPath = dirPath;
            return this;
        }

        public InstaBot fileName(String prefix) {
            this.mName = prefix;
            return this;
        }

        public InstaBot createHistory() {
            // TODO: 11/12/2018 maintain database for downloaded media.
            return this;
        }

        public InstaBot downloadInBackground() {
            // TODO: 11/12/2018 start downloading as soon as valid link copied
            return this;
        }

        public Bot engage() {
            return new Bot(this);
        }

        public InstaBot instaResult(BotCallBack l) {
            this.mCallback = l;
            return this;
        }
    }

}
