/*
 * Copyright (c) 2011-14 Miles Sabin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package shapeless

class TypeableTests {
  import java.{ lang => jl }

  import org.junit.Test
  import org.junit.Assert._

  import syntax.typeable._
  import test._

  @Test
  def testPrimitives {
    val b: Any = 23.toByte
    val cb = b.cast[Byte]
    assertTrue(cb.isDefined)

    val s: Any = 23.toShort
    val cs = s.cast[Short]
    assertTrue(cs.isDefined)

    val c: Any = 'c'
    val cc = c.cast[Char]
    assertTrue(cc.isDefined)

    val i: Any = 23
    val ci = i.cast[Int]
    assertTrue(ci.isDefined)

    val l: Any = 23L
    val cl = l.cast[Long]
    assertTrue(cl.isDefined)

    val f: Any = 23.0F
    val cf = f.cast[Float]
    assertTrue(cf.isDefined)

    val d: Any = 23.0
    val cd = d.cast[Double]
    assertTrue(cd.isDefined)

    val bl: Any = true
    val cbl = bl.cast[Boolean]
    assertTrue(cbl.isDefined)

    val u: Any = ()
    val cu = u.cast[Unit]
    assertTrue(cu.isDefined)
  }

  @Test
  def testBoxedPrimitives {
    val b: Any = 23.toByte
    val cb = b.cast[jl.Byte]
    assertTrue(cb.isDefined)

    val s: Any = 23.toShort
    val cs = s.cast[jl.Short]
    assertTrue(cs.isDefined)

    val c: Any = 'c'
    val cc = c.cast[jl.Character]
    assertTrue(cc.isDefined)

    val i: Any = 23
    val ci = i.cast[jl.Integer]
    assertTrue(ci.isDefined)

    val l: Any = 23L
    val cl = l.cast[jl.Long]
    assertTrue(cl.isDefined)

    val f: Any = 23.0F
    val cf = f.cast[jl.Float]
    assertTrue(cf.isDefined)

    val d: Any = 23.0
    val cd = d.cast[jl.Double]
    assertTrue(cd.isDefined)

    val bl: Any = true
    val cbl = bl.cast[jl.Boolean]
    assertTrue(cbl.isDefined)
  }

  @Test
  def testUnerased {
    val li: Any = List(1, 2, 3, 4)
    val cli = li.cast[List[Int]]
    assertTrue(cli.isDefined)

    val cli2 = li.cast[List[String]]
    assertTrue(cli2.isEmpty)

    val ls: Any = List("foo", "bar", "baz")
    val cls = ls.cast[List[String]]
    assertTrue(cls.isDefined)

    val cls2 = ls.cast[List[Int]]
    assertTrue(cls2.isEmpty)

    val lvs: Any = List(Vector("foo", "bar", "baz"), Vector("wibble"))
    val clvs = lvs.cast[List[Vector[String]]]
    assertTrue(clvs.isDefined)

    val clvs2 = lvs.cast[List[Vector[Int]]]
    assertTrue(clvs2.isEmpty)

    val clvs3 = lvs.cast[List[List[String]]]
    assertTrue(clvs3.isEmpty)

    val ln: Any = Nil
    val cln = ln.cast[List[Int]]
    assert(cln.isDefined)

    val cln2 = ln.cast[List[String]]
    assert(cln2.isDefined)

    val si: Any = Set(1, 2, 3, 4)
    val csi = si.cast[Set[Int]]
    assertTrue(csi.isDefined)

    val csi2 = si.cast[Set[String]]
    assertTrue(csi2.isEmpty)
  }

  trait Poly[T]

  @Test
  def testErased {
    illTyped("""
      Typeable[Int => String]
    """)

    illTyped("""
      Typeable[Poly[Int]]
    """)
  }

  @Test
  def testHList {
    val lisdb: Any = 23 :: "foo" :: 2.0 :: false :: HNil
    val clisdb = lisdb.cast[Int :: String :: Double :: Boolean :: HNil]
    assertTrue(clisdb.isDefined)

    val clisdb2 = lisdb.cast[Int :: String :: Float :: Boolean :: HNil]
    assertTrue(clisdb2.isEmpty)
  }

  @Test
  def testCoproductt {
    type CP = Int :+: String :+: Double :+: Boolean :+: CNil
    type CP2 = Char :+: Long :+: Unit :+: CNil

    val cpi: Any = Coproduct[CP](23)
    val ccpi = cpi.cast[CP]
    assertTrue(ccpi.isDefined)

    val cps: Any = Coproduct[CP]("foo")
    val ccps = cps.cast[CP]
    assertTrue(ccps.isDefined)

    val cpd: Any = Coproduct[CP](2.0)
    val ccpd = cpd.cast[CP]
    assertTrue(ccpd.isDefined)

    val cpb: Any = Coproduct[CP](true)
    val ccpb = cpb.cast[CP]
    assertTrue(ccpb.isDefined)

    val cpc: Any = Coproduct[CP2]('c')
    val ccpc = cpc.cast[CP]
    assertTrue(ccpc.isEmpty)

    val cpl: Any = Coproduct[CP2](13L)
    val ccpl = cpl.cast[CP]
    assertTrue(ccpl.isEmpty)

    val cpu: Any = Coproduct[CP2](())
    val ccpu = cpu.cast[CP]
    assertTrue(ccpu.isEmpty)
  }

  @Test
  def testAnys {
    val v: Any = 23
    val cv = v.cast[AnyVal]
    assertTrue(cv.isDefined)

    val cv2 = v.cast[AnyRef]
    assertTrue(cv2.isEmpty)

    val r: Any = "foo"
    val cr = r.cast[AnyRef]
    assertTrue(cr.isDefined)

    val cr2 = r.cast[AnyVal]
    assertTrue(cr2.isEmpty)
  }

  @Test
  def testNull {
    val n: Any = null
    val cn = n.cast[AnyVal]
    assertTrue(!cn.isDefined)

    val cn1 = n.cast[AnyRef]
    assertTrue(!cn1.isDefined)

    val cn2 = n.cast[Int]
    assertTrue(!cn2.isDefined)

    val cn3 = n.cast[String]
    assertTrue(!cn3.isDefined)

    val cn4 = n.cast[List[Int]]
    assertTrue(!cn4.isDefined)

    val cn5 = n.cast[HNil]
    assertTrue(!cn5.isDefined)

    val cn6 = n.cast[Int :: String :: Boolean :: HNil]
    assertTrue(!cn6.isDefined)

    val cn7 = n.cast[(Int, String)]
    assertTrue(!cn7.isDefined)
  }

  @Test
  def testExistentials {
    val l: Any = List(1, 2, 3, 4)
    val cl = l.cast[List[_]]
    assertTrue(cl.isDefined)

    val cl2 = l.cast[Vector[_]]
    assertTrue(cl2.isEmpty)
  }

  @Test
  def testTraits {
    trait A
    trait B
    trait C
    class D extends A with B

    val d: Any = new D
    val cd = d.cast[A]
    assertTrue(cd.isDefined)

    val cd2 = d.cast[B]
    assertTrue(cd2.isDefined)

    val cd3 = d.cast[C]
    assertTrue(cd3.isEmpty)
  }

  @Test
  def testIntersections {
    trait A
    trait B
    trait C
    class D extends A with B

    val d: Any = new D
    val cd = d.cast[A with B]
    assertTrue(cd.isDefined)

    val cd2 = d.cast[B with A]
    assertTrue(cd2.isDefined)

    val cd3 = d.cast[A with C]
    assertTrue(cd3.isEmpty)

    val cd4 = d.cast[C with A]
    assertTrue(cd4.isEmpty)
  }

  @Test
  def testTuples {
    val p: Any = (23, "foo")
    val cp = p.cast[(Int, String)]
    assertTrue(cp.isDefined)

    val cp2 = p.cast[(Double, String)]
    assertTrue(cp2.isEmpty)

    val cp3 = p.cast[(Int, List[String])]
    assertTrue(cp3.isEmpty)

    val m: Any = Map(1 -> "1", 2 -> "2", 3 -> "3")
    val cm = m.cast[Map[Int, String]]
    assertTrue(cm.isDefined)

    val cm2 = m.cast[Map[Double, String]]
    assertTrue(cm2.isEmpty)

    val cm3 = m.cast[Map[Int, List[String]]]
    assertTrue(cm3.isEmpty)
  }

  @Test
  def testOption {
    val o: Any = Option(23)
    val co = o.cast[Option[Int]]
    assertTrue(co.isDefined)

    val co2 = o.cast[Option[String]]
    assertTrue(co2.isEmpty)

    val co3 = o.cast[Option[Any]]
    assertTrue(co3.isDefined)

    val co4 = o.cast[Option[_]]
    assertTrue(co4.isDefined)
  }

  @Test
  def testEither {
    val ei: Any = Left[Int, String](23)
    val cei = ei.cast[Either[Int, String]]
    assertTrue(cei.isDefined)

    val cei2 = ei.cast[Left[Int, String]]
    assertTrue(cei2.isDefined)

    val cei3 = ei.cast[Either[Int, _]]
    assertTrue(cei3.isDefined)

    val cei4 = ei.cast[Either[Double, String]]
    assertTrue(cei4.isEmpty)

    val es: Any = Right[Int, String]("foo")
    val ces = es.cast[Either[Int, String]]
    assertTrue(ces.isDefined)

    val ces2 = es.cast[Right[Int, String]]
    assertTrue(ces2.isDefined)

    val ces3 = es.cast[Either[_, String]]
    assertTrue(ces3.isDefined)

    val ces4 = es.cast[Either[Int, Unit]]
    assertTrue(ces4.isEmpty)
  }

  case class Foo(i: Int, s: String, b: Boolean)
  case class Bar[T](t: T)

  @Test
  def testProducts {
    val foo: Any = Foo(23, "foo", true)
    val iBar: Any = Bar(23)
    val sBar: Any = Bar("bar")

    val cfoo1 = foo.cast[Foo]
    assertTrue(cfoo1.isDefined)

    val cfoo2 = iBar.cast[Foo]
    assertTrue(cfoo2.isEmpty)

    val cbar1 = iBar.cast[Bar[Int]]
    assertTrue(cbar1.isDefined)

    val cbar2 = sBar.cast[Bar[String]]
    assertTrue(cbar2.isDefined)

    val cbar3 = iBar.cast[Bar[String]]
    assertTrue(cbar3.isEmpty)

    val cbar4 = sBar.cast[Bar[Int]]
    assertTrue(cbar4.isEmpty)
  }

  @Test
  def testTypeCase {
    import HList.ListCompat._

    def typeCase[T: Typeable](t: Any): Option[T] = {
      val T = TypeCase[T]
      val `List[T]` = TypeCase[List[T]]
      val `(String, T)` = TypeCase[(String, T)]
      val `List[(String, T)]` = TypeCase[List[(String, T)]]

      t match {
        case T(t) => Some(t)
        case `List[T]`(lt) => lt.headOption
        case `(String, T)`(s, t) => typed[String](s) ; Some(t)
        case `List[(String, T)]`((s, t) :: _) => typed[String](s); Some(t)
        case `List[(String, T)]`(lst) => assertTrue(lst.isEmpty) ; None
        case _ => None
      }
    }

    assertEquals(Some(23), typeCase[Int](23: Any))
    assertEquals(None, typeCase[String](23: Any))
    assertEquals(Some(23), typeCase[Int](List(23): Any))
    assertEquals(None, typeCase[String](List(23): Any))
    assertEquals(Some(23), typeCase[Int](("foo", 23): Any))
    assertEquals(None, typeCase[String](("foo", 23): Any))
    assertEquals(Some(23), typeCase[Int](List(("foo", 23)): Any))
    assertEquals(None, typeCase[String](List(("foo", 23)): Any))
  }
}
