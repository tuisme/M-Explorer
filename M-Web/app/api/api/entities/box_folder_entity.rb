module Api::Entities
  class BoxFolderEntity < Grape::Entity
    expose :id
    expose :name
    expose :has_thumbnail
    expose :thumbnail_link
    expose :created_time
    expose :type, as: :mime_type
    expose :size

    private
    def has_thumbnail
        false
    end
    def thumbnail_link
        nil
    end
  end
end
