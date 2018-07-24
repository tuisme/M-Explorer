class CreateClouds < ActiveRecord::Migration[5.2]
  def change
    create_table :clouds do |t|
      t.references :user, foreign_key: true
      t.string :cid
      t.string :cname
      t.string :ctype
      t.string :ctoken
      t.string :used
      t.string :unused
      t.string :cident

      t.timestamps
    end
  end
end
