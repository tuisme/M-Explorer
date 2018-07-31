module Api::Entities
  class DropboxFolderEntity < Grape::Entity
    expose :path_lower, as: :id
    expose :name, as: :name
    expose :has_thumbnail
    expose :thumbnail_link
    expose :created_time
    expose :mime_type do |instance, options|
      instance.instance_of?(Dropbox::FileMetadata) ? "file" : "folder"
    end
    expose :size

    private
    def has_thumbnail
        false
    end
    def thumbnail_link
        nil
    end
    def created_time
        nil
    end
    def size
      nil
    end
  end
end
