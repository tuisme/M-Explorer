class CreateClouds < ActiveRecord::Migration[5.2]
  def change
    create_table :clouds do |t|
      t.references :user, foreign_key: true
      t.string :cloud_root
      t.string :cloud_name
      t.string :cloud_type
      t.string :access_token
      t.string :refresh_token
      t.string :used
      t.string :allocated
      t.string :provider_id

      t.timestamps
    end
  end
end
