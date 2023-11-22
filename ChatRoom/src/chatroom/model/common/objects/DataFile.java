package chatroom.model.common.objects;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;

/**
 * Class used ot send a file
 */
public class DataFile extends Data implements Serializable {
  private final String name;
  private final byte[] content;
  private static final long serialVersionUID = 4736565073208578644L;

  /**
   * Constructor
   * @param path the path of the file
   * @param priv true if the message is private
   * @throws IOException threw if there is a problem with the specified file
   */
  public DataFile(String path, boolean priv) throws IOException {
    super(DataType.FILE, priv);
    File file = new File(path);
    this.name = file.getName();
    this.content = Files.readAllBytes(file.toPath());
  }

  /**
   * Getter
   * @return the file's name
   */
  public String getName() {
    return name;
  }

  /**
   * Getter
   * @return the file's content
   */
  public byte[] getContent() {
    return content;
  }
}
