package lib.gintec_rdl.spector;

import lib.gintec_rdl.spector.TypeInfo;

import java.io.File;

public interface FileInspector {

    TypeInfo getContentType(File file);
}
