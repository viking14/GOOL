/* Generated By:JJTree: Do not edit this line. SUPERCLASS_INIT.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package gool.parser.cpp;

public
class SUPERCLASS_INIT extends SimpleNode {
  public SUPERCLASS_INIT(int id) {
    super(id);
  }

  public SUPERCLASS_INIT(CPPParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(CPPParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=bdee87786bf05f0c82eaea1634dea7e1 (do not edit this line) */