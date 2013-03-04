/* Generated By:JavaCC: Do not edit this line. Parser.java */
package org.net9.simplex.ppmc.prop.javacc;

import java.io.Reader;
import java.io.StringReader;

import org.net9.simplex.ppmc.prop.*;

@SuppressWarnings("all")
public class Parser implements ParserConstants {

  public Parser ()
  {
    this((Reader)null);
  }
  public StateProperty parse(String s) throws java.text.ParseException
  {
    this.ReInit(new StringReader(s));
    try {
      StateProperty p = this.start();
      return p;
    } catch (ParseException e) {
      throw new PropertyParseException(e);
    } catch (TokenMgrError e) {
      throw new PropertyParseException(e);
    } finally
    {
      this.ReInit((Reader)null);
    }
  }

  final public StateProperty start() throws ParseException {
  StateProperty p;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case DELIM:
      jj_consume_token(DELIM);
      break;
    default:
      jj_la1[0] = jj_gen;
      ;
    }
    p = stateFormula();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case DELIM:
      jj_consume_token(DELIM);
      break;
    default:
      jj_la1[1] = jj_gen;
      ;
    }
    {if (true) return p;}
    throw new Error("Missing return statement in function");
  }

  final public StateProperty stateFormula() throws ParseException {
  StateProperty p1,p2;
  PropOr p = null;
    p1 = stateTerm();
    label_1:
    while (true) {
      if (jj_2_1(2)) {
        ;
      } else {
        break label_1;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case DELIM:
        jj_consume_token(DELIM);
        break;
      default:
        jj_la1[2] = jj_gen;
        ;
      }
      jj_consume_token(OR);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case DELIM:
        jj_consume_token(DELIM);
        break;
      default:
        jj_la1[3] = jj_gen;
        ;
      }
      p2 = stateTerm();
            if (p==null) {
                p = new PropOr(p1,p2);
                p1 = p;
            }
            else {
                p.item.add(p2);
            }
    }
    {if (true) return p1;}
    throw new Error("Missing return statement in function");
  }

  final public StateProperty stateTerm() throws ParseException {
  StateProperty p1,p2;
  PropAnd p = null;
    p1 = stateElement();
    label_2:
    while (true) {
      if (jj_2_2(2)) {
        ;
      } else {
        break label_2;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case DELIM:
        jj_consume_token(DELIM);
        break;
      default:
        jj_la1[4] = jj_gen;
        ;
      }
      jj_consume_token(AND);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case DELIM:
        jj_consume_token(DELIM);
        break;
      default:
        jj_la1[5] = jj_gen;
        ;
      }
      p2 = stateElement();
        if (p==null) {
                p = new PropAnd(p1,p2);
                p1 = p;
        } else {
                p.item.add(p2);
        }
    }
    {if (true) return p1;}
    throw new Error("Missing return statement in function");
  }

  final public StateProperty stateElement() throws ParseException {
  StateProperty p;
  Token t;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 18:
      jj_consume_token(18);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case DELIM:
        jj_consume_token(DELIM);
        break;
      default:
        jj_la1[6] = jj_gen;
        ;
      }
      p = stateFormula();
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case DELIM:
        jj_consume_token(DELIM);
        break;
      default:
        jj_la1[7] = jj_gen;
        ;
      }
      jj_consume_token(19);
    {if (true) return p;}
      break;
    case NOT:
      jj_consume_token(NOT);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case DELIM:
        jj_consume_token(DELIM);
        break;
      default:
        jj_la1[8] = jj_gen;
        ;
      }
      p = stateFormula();
    {if (true) return new PropNot(p);}
      break;
    case TRUE:
      jj_consume_token(TRUE);
    {if (true) return new PropTrue();}
      break;
    case FALSE:
      jj_consume_token(FALSE);
                {if (true) return new PropNot(new PropTrue());}
      break;
    case IDENT:
      t = jj_consume_token(IDENT);
        {if (true) return new PropAtom(t.image);}
      break;
    case 20:
      jj_consume_token(20);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case DELIM:
        jj_consume_token(DELIM);
        break;
      default:
        jj_la1[9] = jj_gen;
        ;
      }
      t = jj_consume_token(INTEGER);
    PropSet ps = new PropSet();
    ps.item.add(Integer.parseInt(t.image));
      label_3:
      while (true) {
        if (jj_2_3(2)) {
          ;
        } else {
          break label_3;
        }
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case DELIM:
          jj_consume_token(DELIM);
          break;
        default:
          jj_la1[10] = jj_gen;
          ;
        }
        jj_consume_token(21);
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case DELIM:
          jj_consume_token(DELIM);
          break;
        default:
          jj_la1[11] = jj_gen;
          ;
        }
        t = jj_consume_token(INTEGER);
    ps.item.add(Integer.parseInt(t.image));
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case DELIM:
        jj_consume_token(DELIM);
        break;
      default:
        jj_la1[12] = jj_gen;
        ;
      }
      jj_consume_token(22);
    {if (true) return ps;}
      break;
    case PROB:
      jj_consume_token(PROB);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case DELIM:
        jj_consume_token(DELIM);
        break;
      default:
        jj_la1[13] = jj_gen;
        ;
      }
      t = jj_consume_token(COMP);
    String comparator = t.image;
    PathProperty path;
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case DELIM:
        jj_consume_token(DELIM);
        break;
      default:
        jj_la1[14] = jj_gen;
        ;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case FLOAT:
        t = jj_consume_token(FLOAT);
        break;
      case INTEGER:
        t = jj_consume_token(INTEGER);
        break;
      default:
        jj_la1[15] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    double prob = Double.parseDouble(t.image);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case DELIM:
        jj_consume_token(DELIM);
        break;
      default:
        jj_la1[16] = jj_gen;
        ;
      }
      jj_consume_token(18);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case DELIM:
        jj_consume_token(DELIM);
        break;
      default:
        jj_la1[17] = jj_gen;
        ;
      }
      path = pathFormula();
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case DELIM:
        jj_consume_token(DELIM);
        break;
      default:
        jj_la1[18] = jj_gen;
        ;
      }
      jj_consume_token(19);
    {if (true) return new PropProb(path, comparator, prob);}
      break;
    default:
      jj_la1[19] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  final public PathProperty pathFormula() throws ParseException {
  StateProperty p1,p2;
  Token t;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case EVENTUALLY:
      jj_consume_token(EVENTUALLY);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case DELIM:
        jj_consume_token(DELIM);
        break;
      default:
        jj_la1[20] = jj_gen;
        ;
      }
      p1 = stateFormula();
    {if (true) return new PropEventually(p1);}
      break;
    case ALWAYS:
      jj_consume_token(ALWAYS);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case DELIM:
        jj_consume_token(DELIM);
        break;
      default:
        jj_la1[21] = jj_gen;
        ;
      }
      p1 = stateFormula();
    {if (true) return new PropAlways(p1);}
      break;
    case NEXT:
      jj_consume_token(NEXT);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case DELIM:
        jj_consume_token(DELIM);
        break;
      default:
        jj_la1[22] = jj_gen;
        ;
      }
      p1 = stateFormula();
    {if (true) return new PropNext(p1);}
      break;
    case NOT:
    case TRUE:
    case FALSE:
    case PROB:
    case IDENT:
    case 18:
    case 20:
      p1 = stateFormula();
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case DELIM:
        jj_consume_token(DELIM);
        break;
      default:
        jj_la1[23] = jj_gen;
        ;
      }
      jj_consume_token(UNTIL);
    boolean bounded = false;
    int bound = 0;
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case DELIM:
        jj_consume_token(DELIM);
        break;
      default:
        jj_la1[24] = jj_gen;
        ;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case COMP:
        t = jj_consume_token(COMP);
      if (!t.image.equals("<")) {if (true) throw generateParseException();}
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case DELIM:
          jj_consume_token(DELIM);
          break;
        default:
          jj_la1[25] = jj_gen;
          ;
        }
        t = jj_consume_token(INTEGER);
          bounded = true;
          bound = Integer.parseInt(t.image);
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case DELIM:
          jj_consume_token(DELIM);
          break;
        default:
          jj_la1[26] = jj_gen;
          ;
        }
        break;
      default:
        jj_la1[27] = jj_gen;
        ;
      }
      p2 = stateFormula();
     if (bounded)
     {
       {if (true) return new PropBoundedUntil(p1,p2,bound);}
     } else
     {
       {if (true) return new PropUntil(p1,p2);}
     }
      break;
    default:
      jj_la1[28] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  private boolean jj_2_1(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_1(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(0, xla); }
  }

  private boolean jj_2_2(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_2(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(1, xla); }
  }

  private boolean jj_2_3(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_3(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(2, xla); }
  }

  private boolean jj_3R_9() {
    if (jj_scan_token(FALSE)) return true;
    return false;
  }

  private boolean jj_3R_8() {
    if (jj_scan_token(TRUE)) return true;
    return false;
  }

  private boolean jj_3R_7() {
    if (jj_scan_token(NOT)) return true;
    return false;
  }

  private boolean jj_3_3() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_scan_token(2)) jj_scanpos = xsp;
    if (jj_scan_token(21)) return true;
    xsp = jj_scanpos;
    if (jj_scan_token(2)) jj_scanpos = xsp;
    if (jj_scan_token(INTEGER)) return true;
    return false;
  }

  private boolean jj_3_1() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_scan_token(2)) jj_scanpos = xsp;
    if (jj_scan_token(OR)) return true;
    xsp = jj_scanpos;
    if (jj_scan_token(2)) jj_scanpos = xsp;
    if (jj_3R_4()) return true;
    return false;
  }

  private boolean jj_3_2() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_scan_token(2)) jj_scanpos = xsp;
    if (jj_scan_token(AND)) return true;
    xsp = jj_scanpos;
    if (jj_scan_token(2)) jj_scanpos = xsp;
    if (jj_3R_5()) return true;
    return false;
  }

  private boolean jj_3R_5() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_6()) {
    jj_scanpos = xsp;
    if (jj_3R_7()) {
    jj_scanpos = xsp;
    if (jj_3R_8()) {
    jj_scanpos = xsp;
    if (jj_3R_9()) {
    jj_scanpos = xsp;
    if (jj_3R_10()) {
    jj_scanpos = xsp;
    if (jj_3R_11()) {
    jj_scanpos = xsp;
    if (jj_3R_12()) return true;
    }
    }
    }
    }
    }
    }
    return false;
  }

  private boolean jj_3R_6() {
    if (jj_scan_token(18)) return true;
    return false;
  }

  private boolean jj_3R_4() {
    if (jj_3R_5()) return true;
    return false;
  }

  private boolean jj_3R_11() {
    if (jj_scan_token(20)) return true;
    return false;
  }

  private boolean jj_3R_10() {
    if (jj_scan_token(IDENT)) return true;
    return false;
  }

  private boolean jj_3R_12() {
    if (jj_scan_token(PROB)) return true;
    return false;
  }

  /** Generated Token Manager. */
  public ParserTokenManager token_source;
  SimpleCharStream jj_input_stream;
  /** Current token. */
  public Token token;
  /** Next token. */
  public Token jj_nt;
  private int jj_ntk;
  private Token jj_scanpos, jj_lastpos;
  private int jj_la;
  private int jj_gen;
  final private int[] jj_la1 = new int[29];
  static private int[] jj_la1_0;
  static {
      jj_la1_init_0();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0x4,0x4,0x4,0x4,0x4,0x4,0x4,0x4,0x4,0x4,0x4,0x4,0x4,0x4,0x4,0x18000,0x4,0x4,0x4,0x1601c8,0x4,0x4,0x4,0x4,0x4,0x4,0x4,0x2000,0x160fc8,};
   }
  final private JJCalls[] jj_2_rtns = new JJCalls[3];
  private boolean jj_rescan = false;
  private int jj_gc = 0;

  /** Constructor with InputStream. */
  public Parser(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public Parser(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new ParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 29; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 29; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor. */
  public Parser(java.io.Reader stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new ParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 29; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 29; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor with generated Token Manager. */
  public Parser(ParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 29; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(ParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 29; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      if (++jj_gc > 100) {
        jj_gc = 0;
        for (int i = 0; i < jj_2_rtns.length; i++) {
          JJCalls c = jj_2_rtns[i];
          while (c != null) {
            if (c.gen < jj_gen) c.first = null;
            c = c.next;
          }
        }
      }
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }

  static private final class LookaheadSuccess extends java.lang.Error { }
  final private LookaheadSuccess jj_ls = new LookaheadSuccess();
  private boolean jj_scan_token(int kind) {
    if (jj_scanpos == jj_lastpos) {
      jj_la--;
      if (jj_scanpos.next == null) {
        jj_lastpos = jj_scanpos = jj_scanpos.next = token_source.getNextToken();
      } else {
        jj_lastpos = jj_scanpos = jj_scanpos.next;
      }
    } else {
      jj_scanpos = jj_scanpos.next;
    }
    if (jj_rescan) {
      int i = 0; Token tok = token;
      while (tok != null && tok != jj_scanpos) { i++; tok = tok.next; }
      if (tok != null) jj_add_error_token(kind, i);
    }
    if (jj_scanpos.kind != kind) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) throw jj_ls;
    return false;
  }


/** Get the next Token. */
  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

/** Get the specific Token. */
  final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
  private int[] jj_expentry;
  private int jj_kind = -1;
  private int[] jj_lasttokens = new int[100];
  private int jj_endpos;

  private void jj_add_error_token(int kind, int pos) {
    if (pos >= 100) return;
    if (pos == jj_endpos + 1) {
      jj_lasttokens[jj_endpos++] = kind;
    } else if (jj_endpos != 0) {
      jj_expentry = new int[jj_endpos];
      for (int i = 0; i < jj_endpos; i++) {
        jj_expentry[i] = jj_lasttokens[i];
      }
      jj_entries_loop: for (java.util.Iterator<?> it = jj_expentries.iterator(); it.hasNext();) {
        int[] oldentry = (int[])(it.next());
        if (oldentry.length == jj_expentry.length) {
          for (int i = 0; i < jj_expentry.length; i++) {
            if (oldentry[i] != jj_expentry[i]) {
              continue jj_entries_loop;
            }
          }
          jj_expentries.add(jj_expentry);
          break jj_entries_loop;
        }
      }
      if (pos != 0) jj_lasttokens[(jj_endpos = pos) - 1] = kind;
    }
  }

  /** Generate ParseException. */
  public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[23];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 29; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 23; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
    jj_endpos = 0;
    jj_rescan_token();
    jj_add_error_token(0, 0);
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = jj_expentries.get(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  /** Enable tracing. */
  final public void enable_tracing() {
  }

  /** Disable tracing. */
  final public void disable_tracing() {
  }

  private void jj_rescan_token() {
    jj_rescan = true;
    for (int i = 0; i < 3; i++) {
    try {
      JJCalls p = jj_2_rtns[i];
      do {
        if (p.gen > jj_gen) {
          jj_la = p.arg; jj_lastpos = jj_scanpos = p.first;
          switch (i) {
            case 0: jj_3_1(); break;
            case 1: jj_3_2(); break;
            case 2: jj_3_3(); break;
          }
        }
        p = p.next;
      } while (p != null);
      } catch(LookaheadSuccess ls) { }
    }
    jj_rescan = false;
  }

  private void jj_save(int index, int xla) {
    JJCalls p = jj_2_rtns[index];
    while (p.gen > jj_gen) {
      if (p.next == null) { p = p.next = new JJCalls(); break; }
      p = p.next;
    }
    p.gen = jj_gen + xla - jj_la; p.first = token; p.arg = xla;
  }

  static final class JJCalls {
    int gen;
    Token first;
    int arg;
    JJCalls next;
  }

}
