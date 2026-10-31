import { sqliteTable, text, integer } from 'drizzle-orm/sqlite-core';
export const records = sqliteTable('records', {
 id: text('id').primaryKey(), kind: text('kind').notNull(), payload: text('payload').notNull(), createdAt: integer('created_at').notNull()
});
