package org.gxc

class BuildServer implements Serializable {

  def Node = null;

  def Host = "";
  def User = "";
  def CertPath = "";

  BuildServer(host, user) {
    this.Host = host;
    this.User = user;
  }

  public runCommand(def command) {
    try {
      def cmd = String.format('ssh -o "StrictHostKeyChecking no" -i %s %s@%s "%s"', this.CertPath, this.User, this.Host, command)
      this.Node.sh "${cmd}"
      return true;
    } catch (err) {
      this.Node.echo "Error: runCommand - " + err
      return false;
    }
  }

  public scpSend(def localPath, def remotePath, def isFolder) {
    try {
      def cmd;
      if (isFolder) {
        cmd = 'scp -r -o "StrictHostKeyChecking no" -i %s %s %s@%s:%s';
      } else {
        cmd = 'scp -o "StrictHostKeyChecking no" -i %s %s %s@%s:%s';
      }

      cmd = String.format(cmd, this.CertPath, localPath, this.User, this.Host, remotePath)
      this.Node.sh "${cmd}"
      return true;
    } catch (err) {
      this.Node.echo "Error: scpSend - " + err
      return false;
    }
  }

  public scpGet(def localPath, def remotePath, def isFolder) {
    try {
      def cmd;
      if (isFolder) {
        cmd = 'scp -r -o "StrictHostKeyChecking no" -i %s %s@%s:%s %s';
      } else {
        cmd = 'scp -o "StrictHostKeyChecking no" -i %s %s@%s:%s %s';
      }

      cmd = String.format(cmd, this.CertPath, this.User, this.Host, remotePath, localPath)
      this.Node.sh "${cmd}"
      return true;
    } catch (err) {
      this.Node.echo "Error: scpGet - " + err
      return false;
    }
  }
  
  private runMSBuildTask(def task, def parameters, def msBuildFile) {
    def paramsString = ""
    def mapSize = parameters.size() - 1

    parameters.eachWithIndex { key, value, index ->
      paramsString = (index == mapSize) ? paramsString + "$key=$value" : paramsString + "$key=$value;"
    }
    
    msBuildFile = msBuildFile.replace("\\", "\\\\")
    paramsString = paramsString.replace("\\", "\\\\")

    def command = 'ssh -o "StrictHostKeyChecking no" -i %s %s@%s "cd C:\\_pipeline && RunMsBuildTask.bat %s %s \\\"%s\\\""'
    command = String.format(command, this.CertPath, this.User, this.Host, msBuildFile, task, paramsString)
    this.Node.sh "${command}"
  }

  // Setters
  public setNode(node) {
    this.Node = node;
  }

  public setHost(host) {
    this.Host = host;
  }

  public setUser(user) {
    this.User = user;
  }

  public setCertPath(certPath) {
    this.CertPath = certPath;
  }

}
