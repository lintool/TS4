package ts4.ts4_core.tweets.util;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.SlowCompositeReaderWrapper;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import cc.twittertools.index.IndexStatuses.StatusField;
import edu.umd.cloud9.util.map.HMapIV;
import edu.umd.cloud9.util.map.HMapKI;

public class TermStatistics {
	private final HMapKI<String> term2Id = new HMapKI<String>(); 
	private final HMapIV<Long> id2Freq = new HMapIV<Long>();

	public TermStatistics(String path) throws IOException {
		IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(path)));
		Terms terms = SlowCompositeReaderWrapper.wrap(reader).terms(StatusField.TEXT.name);
		TermsEnum termsEnum = terms.iterator(TermsEnum.EMPTY);

		int termCnt = 1;
		BytesRef bytes = new BytesRef();
		while ( (bytes = termsEnum.next()) != null) {
			byte[] buf = new byte[bytes.length];
			System.arraycopy(bytes.bytes, 0, buf, 0, bytes.length);
			String term = new String(buf, "UTF-8");
			long freq = termsEnum.totalTermFreq();
			term2Id.put(term, termCnt);
			id2Freq.put(termCnt, freq);
			termCnt++;
		}
	}

	public int getId(String term) {
		return term2Id.get(term);
	}

	public long getFreq(int id) {
		return id2Freq.get(id);
	}

	public int getVocabSize() {
		return term2Id.size();
	}
}