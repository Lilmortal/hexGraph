package nz.co.hexgraph.reader;

import nz.co.hexgraph.config.FileType;

public class ReaderFactory {
    public static Reader create(FileType fileType) {
        Reader reader = null;
        switch (fileType) {
            case FILE:
                reader = new FileReader();
                break;
            case S3:
                reader = new S3Reader();
                break;
            default:
                throw new RuntimeException("Invalid file type.");
        }

        return reader;
    }
}
