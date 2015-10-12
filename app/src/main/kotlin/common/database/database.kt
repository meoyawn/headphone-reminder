package common.database

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.squareup.sqlbrite.QueryObservable
import com.squareup.sqlbrite.SqlBrite
import common.thread.assertWorkerThread
import rx.Observable
import rx.schedulers.Schedulers

inline fun <T> inTransaction(bd: SQLiteDatabase, f: SQLiteDatabase.() -> T): T =
    run {
      assertWorkerThread()

      bd.beginTransaction()
      try {
        bd.f().apply { bd.setTransactionSuccessful() }
      } finally {
        bd.endTransaction()
      }
    }

private fun runQuery(q: SqlBrite.Query): Cursor =
    run {
      assertWorkerThread()

      q.run()
    }

private fun cursors(qo: QueryObservable): Observable<Cursor> =
    qo.map(::runQuery).subscribeOn(Schedulers.io())
